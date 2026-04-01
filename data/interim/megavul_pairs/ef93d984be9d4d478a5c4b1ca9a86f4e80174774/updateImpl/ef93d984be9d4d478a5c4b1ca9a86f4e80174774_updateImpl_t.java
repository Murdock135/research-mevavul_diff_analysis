class updateImpl {
@Override
	public KBTemplate updateImpl(KBTemplate kbTemplate) {
		kbTemplate = toUnwrappedModel(kbTemplate);

		boolean isNew = kbTemplate.isNew();

		KBTemplateModelImpl kbTemplateModelImpl = (KBTemplateModelImpl)kbTemplate;

		if (Validator.isNull(kbTemplate.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			kbTemplate.setUuid(uuid);
		}

		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (kbTemplate.getCreateDate() == null)) {
			if (serviceContext == null) {
				kbTemplate.setCreateDate(now);
			}
			else {
				kbTemplate.setCreateDate(serviceContext.getCreateDate(now));
			}
		}

		if (!kbTemplateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kbTemplate.setModifiedDate(now);
			}
			else {
				kbTemplate.setModifiedDate(serviceContext.getModifiedDate(now));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = kbTemplate.getCompanyId();

			long groupId = kbTemplate.getGroupId();

			long kbTemplateId = 0;

			if (!isNew) {
				kbTemplateId = kbTemplate.getPrimaryKey();
			}

			try {
				kbTemplate.setContent(SanitizerUtil.sanitize(companyId,
						groupId, userId, KBTemplate.class.getName(),
						kbTemplateId, ContentTypes.TEXT_HTML,
						Sanitizer.MODE_ALL, kbTemplate.getContent(), null));
			}
			catch (SanitizerException se) {
				throw new SystemException(se);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (kbTemplate.isNew()) {
				session.save(kbTemplate);

				kbTemplate.setNew(false);
			}
			else {
				kbTemplate = (KBTemplate)session.merge(kbTemplate);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!KBTemplateModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else
		 if (isNew) {
			Object[] args = new Object[] { kbTemplateModelImpl.getUuid() };

			finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
			finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
				args);

			args = new Object[] {
					kbTemplateModelImpl.getUuid(),
					kbTemplateModelImpl.getCompanyId()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID_C, args);
			finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID_C,
				args);

			args = new Object[] { kbTemplateModelImpl.getGroupId() };

			finderCache.removeResult(FINDER_PATH_COUNT_BY_GROUPID, args);
			finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_GROUPID,
				args);

			finderCache.removeResult(FINDER_PATH_COUNT_ALL, FINDER_ARGS_EMPTY);
			finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL,
				FINDER_ARGS_EMPTY);
		}

		else {
			if ((kbTemplateModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						kbTemplateModelImpl.getOriginalUuid()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
					args);

				args = new Object[] { kbTemplateModelImpl.getUuid() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
					args);
			}

			if ((kbTemplateModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID_C.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						kbTemplateModelImpl.getOriginalUuid(),
						kbTemplateModelImpl.getOriginalCompanyId()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID_C, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID_C,
					args);

				args = new Object[] {
						kbTemplateModelImpl.getUuid(),
						kbTemplateModelImpl.getCompanyId()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID_C, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID_C,
					args);
			}

			if ((kbTemplateModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_GROUPID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						kbTemplateModelImpl.getOriginalGroupId()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_GROUPID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_GROUPID,
					args);

				args = new Object[] { kbTemplateModelImpl.getGroupId() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_GROUPID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_GROUPID,
					args);
			}
		}

		entityCache.putResult(KBTemplateModelImpl.ENTITY_CACHE_ENABLED,
			KBTemplateImpl.class, kbTemplate.getPrimaryKey(), kbTemplate, false);

		clearUniqueFindersCache(kbTemplateModelImpl, false);
		cacheUniqueFindersCache(kbTemplateModelImpl);

		kbTemplate.resetOriginalValues();

		return kbTemplate;
	}
}
