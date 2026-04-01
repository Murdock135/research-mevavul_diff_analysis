class getItem {
public Item getItem(File file, final FollowLinkType followlinks, List<String> followedLinkPaths ) throws FileIOException
	{
		try
		{
			Path path = file.toPath();

			ItemType type;
			
			if (Files.isSymbolicLink(path))
			{
				String target;
				target = Files.readSymbolicLink(path).toString();
				final String firstChar = target.substring(0, 1);
				if (!firstChar.equals(Item.SEPARATOR))
				{
					if (!firstChar.equals("."))
					{
						target = "." + Item.SEPARATOR + target;
					}
					target = path.toString() + Item.SEPARATOR + target;
				}
				target = Paths.get(target).toFile().getCanonicalPath();

                if (!followlinks.equals(FollowLinkType.NONE) && followlinks.equals(FollowLinkType.EXTERNAL) && !Paths.get(target).toFile().getCanonicalFile().toPath().startsWith(localPath + Item.SEPARATOR) )
				{
                    boolean foundLink = false;
                    for( String followedLinkPath: followedLinkPaths )
                    {
                        // 1. if the link target is a child of a already followed link, then there is no need to follow again
                        // 2. and the target should not be equal with a already followed link. Otherwise we are requesting the same item again. So we have to follow.
                        if( target.startsWith(followedLinkPath) && !target.equals(followedLinkPath) )
                        {
                            foundLink = true;
                            break;
                        }
                    }
                    
                    if( !foundLink )
                    {
                        final Path targetPath = Paths.get(target);
                        if (Files.exists(targetPath, LinkOption.NOFOLLOW_LINKS))
                        {
                            path = targetPath;
                            followedLinkPaths.add(target);
                        }
                    }
				}
			}

			BasicFileAttributes basic_attr = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			final Long filesize = basic_attr.size();
			final FileTime creationTime = basic_attr.creationTime();
			final FileTime modifyTime = basic_attr.lastModifiedTime();
			final FileTime accessTime = basic_attr.lastAccessTime();

			if (basic_attr.isDirectory())
			{
				type = ItemType.FOLDER;
			}
			else if (basic_attr.isRegularFile())
			{
				type = ItemType.FILE;
			}
			else if (basic_attr.isSymbolicLink())
			{
				type = ItemType.LINK;
			}
			else
			{
				type = ItemType.UNKNOWN;
			}

			Map<String, String[]> attributes = new HashMap<>();

			PosixFileAttributeView posixView = Files.getFileAttributeView(path, PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
			if (posixView != null)
			{
				final PosixFileAttributes attr = posixView.readAttributes();
				if (type.equals(ItemType.LINK))
				{
					attributes.put(Item.ATTRIBUTE_POSIX, new String[] { attr.group().getName(), attr.owner().getName() });
				}
				else
				{
					attributes.put(Item.ATTRIBUTE_POSIX, new String[] { attr.group().getName(), attr.owner().getName(),
							fromPermissions(attr.permissions()).toString() });
				}
			}
			else
			{
				DosFileAttributeView dosView = Files.getFileAttributeView(path, DosFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
				if (dosView != null)
				{
					final DosFileAttributes attr = dosView.readAttributes();
					attributes.put(Item.ATTRIBUTE_DOS, new String[] { attr.isArchive() ? "1" : "0", attr.isHidden() ? "1" : "0", attr.isReadOnly() ? "1" : "0",
							attr.isSystem() ? "1" : "0" });
				}
			}

			if (!type.equals(ItemType.LINK))
			{
				AclFileAttributeView aclView = Files.getFileAttributeView(path, AclFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
				if (aclView != null)
				{
					if (!attributes.containsKey(Item.ATTRIBUTE_POSIX)) attributes.put(Item.ATTRIBUTE_OWNER, new String[] { aclView.getOwner().getName() });

					AclFileAttributeView parentAclView = Files.getFileAttributeView(path.getParent(), AclFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);

					List<AclEntry> aclList = getLocalAclEntries(type, parentAclView.getAcl(), aclView.getAcl());
					if (aclList.size() > 0)
					{
						List<String> aclData = new ArrayList<>();
						for (AclEntry acl : aclList)
						{
							List<String> flags = new ArrayList<>();
							for (AclEntryFlag flag : acl.flags())
							{
								flags.add(flag.name());
							}
							List<String> permissions = new ArrayList<>();
							for (AclEntryPermission permission : acl.permissions())
							{
								permissions.add(permission.name());
							}

							aclData.add(acl.type().name());
							aclData.add(acl.principal().getName());
							aclData.add(StringUtils.join(flags, ","));
							aclData.add(StringUtils.join(permissions, ","));
						}
						String[] arr = new String[aclData.size()];
						arr = aclData.toArray(arr);
						attributes.put(Item.ATTRIBUTE_ACL, arr);
					}
				}
				else if (!attributes.containsKey(Item.ATTRIBUTE_POSIX))
				{
					FileOwnerAttributeView ownerView = Files.getFileAttributeView(path, FileOwnerAttributeView.class, LinkOption.NOFOLLOW_LINKS);
					if (ownerView != null)
					{
						attributes.put(Item.ATTRIBUTE_OWNER, new String[] { ownerView.getOwner().getName() });
					}
				}
			}

			return Item.fromLocalData(file.getName(), type, filesize, creationTime, modifyTime, accessTime, attributes);
		}
		catch (final IOException e)
		{
			throw new FileIOException("Can't read attributes of '" + file.getAbsolutePath() + "'", e);
		}
	}
}
