class findAndReplaceQueryDates {
private static String findAndReplaceQueryDates(String query) {
            query = RegEX.replaceAll(query, " ", "\\s{2,}");

            List<RegExMatch> matches = RegEX.find(query, "[\\+\\-\\!\\(]?" + "structureName" + ":(\\S+)\\)?");
            String structureVarName = null;
            if ((matches != null) && (0 < matches.size()))
                structureVarName = matches.get(0).getGroups().get(0).getMatch();

            if (!UtilMethods.isSet(structureVarName)) {
                matches = RegEX.find(query, "[\\+\\-\\!\\(]?" + "structureName".toLowerCase() + ":(\\S+)\\)?");
                if ((matches != null) && (0 < matches.size()))
                    structureVarName = matches.get(0).getGroups().get(0).getMatch();
            }

            if (!UtilMethods.isSet(structureVarName)) {
                Logger.debug(ESContentFactoryImpl.class, "Structure Variable Name not found");
                //return query;
            }

            Structure selectedStructure = CacheLocator.getContentTypeCache().getStructureByVelocityVarName(structureVarName);

            if ((selectedStructure == null) || !InodeUtils.isSet(selectedStructure.getInode())) {
                Logger.debug(ESContentFactoryImpl.class, "Structure not found");
                //return query;
            }

            //delete additional blank spaces on date range
            if(UtilMethods.contains(query, "[ ")) {
                query = query.replace("[ ", "[");
            }

            if(UtilMethods.contains(query, " ]")) {
                query = query.replace(" ]", "]");
            }

            String clausesStr = RegEX.replaceAll(query, "", "[\\+\\-\\(\\)]*");
            String[] tokens = clausesStr.split(" ");
            String token;
            List<String> clauses = new ArrayList<String>();
            for (int pos = 0; pos < tokens.length; ++pos) {
                token = tokens[pos];
                if (token.matches("\\S+\\.\\S+:\\S*")) {
                    clauses.add(token);
                } else if (token.matches("\\d+:\\S*")) {
                    clauses.set(clauses.size() - 1, clauses.get(clauses.size() - 1) + " " + token);
                } else if (token.matches("\\[\\S*")) {
                    clauses.set(clauses.size() - 1, clauses.get(clauses.size() - 1) + token);
                } else if (token.matches("to")) {
                    clauses.set(clauses.size() - 1, clauses.get(clauses.size() - 1) + " " + token);
                } else if (token.matches("\\S*\\]")) {
                    clauses.set(clauses.size() - 1, clauses.get(clauses.size() - 1) + " " + token);
                } else if (token.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                    clauses.set(clauses.size() - 1, clauses.get(clauses.size() - 1) + " " + token);
                } else {
                    clauses.add(token);
                }
            }

            //DOTCMS - 4127
            List<com.dotmarketing.portlets.structure.model.Field> dateFields = new ArrayList<com.dotmarketing.portlets.structure.model.Field>();
            String tempStructureVarName;
            Structure tempStructure;

            for (String clause: clauses) {

                // getting structure names from query
                if(clause.indexOf('.') >= 0 && (clause.indexOf('.') < clause.indexOf(':'))){

                    tempStructureVarName = clause.substring(0, clause.indexOf('.'));
                    tempStructure = CacheLocator.getContentTypeCache().getStructureByVelocityVarName(tempStructureVarName);

                    List<com.dotmarketing.portlets.structure.model.Field> tempStructureFields = FieldsCache.getFieldsByStructureVariableName(tempStructure.getVelocityVarName());

                    for (int pos = 0; pos < tempStructureFields.size();) {

                        if (tempStructureFields.get(pos).getFieldType().equals(com.dotmarketing.portlets.structure.model.Field.FieldType.DATE_TIME.toString()) ||
                                tempStructureFields.get(pos).getFieldType().equals(com.dotmarketing.portlets.structure.model.Field.FieldType.DATE.toString()) ||
                                tempStructureFields.get(pos).getFieldType().equals(com.dotmarketing.portlets.structure.model.Field.FieldType.TIME.toString())) {
                            ++pos;
                        } else {
                            tempStructureFields.remove(pos);
                        }

                    }

                    dateFields.addAll(tempStructureFields);
                }
            }

            String replace;
            for (String clause: clauses) {
                for (com.dotmarketing.portlets.structure.model.Field field: dateFields) {

                    structureVarName = CacheLocator.getContentTypeCache().getStructureByInode(field.getStructureInode()).getVelocityVarName().toLowerCase();

                    if (clause.startsWith(structureVarName + "." + field.getVelocityVarName().toLowerCase() + ":") || clause.startsWith("moddate:")) {
                        replace = new String(clause);
                        if (field.getFieldType().equals(com.dotmarketing.portlets.structure.model.Field.FieldType.DATE_TIME.toString()) || clause.startsWith("moddate:")) {
                            matches = RegEX.find(replace, "\\[(\\d{1,2}/\\d{1,2}/\\d{4}) to ");
                            for (RegExMatch regExMatch : matches) {
                                replace = replace.replace("[" + regExMatch.getGroups().get(0).getMatch() + " to ", "["
                                        + regExMatch.getGroups().get(0).getMatch() + " 00:00:00 to ");
                            }

                            matches = RegEX.find(replace, " to (\\d{1,2}/\\d{1,2}/\\d{4})\\]");
                            for (RegExMatch regExMatch : matches) {
                                replace = replace.replace(" to " + regExMatch.getGroups().get(0).getMatch() + "]", " to "
                                        + regExMatch.getGroups().get(0).getMatch() + " 23:59:59]");
                            }
                        }

                        // Format MM/dd/yyyy hh:mm:ssa
                        replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}(?:AM|PM|am|pm))\\\"?", "MM/dd/yyyy hh:mm:ssa");

                        // Format MM/dd/yyyy hh:mm:ss a
                        replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}\\s+(?:AM|PM|am|pm))\\\"?", "MM/dd/yyyy hh:mm:ss a");

                        // Format MM/dd/yyyy hh:mm a
                        replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2}\\s+(?:AM|PM|am|pm))\\\"?", "MM/dd/yyyy hh:mm a");

                        // Format MM/dd/yyyy hh:mma
                        replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2}(?:AM|PM|am|pm))\\\"?", "MM/dd/yyyy hh:mma");

                        // Format MM/dd/yyyy HH:mm:ss
                        replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})\\\"?", null);

                        // Format MM/dd/yyyy HH:mm
                        replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2})\\\"?", null);

                        // Format MM/dd/yyyy
                        replace = replaceDateWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4})\\\"?");

                        // Format hh:mm:ssa
                        replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2}:\\d{1,2}(?:AM|PM|am|pm))\\\"?", "hh:mm:ssa");

                        // Format hh:mm:ss a
                        replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2}:\\d{1,2}\\s+(?:AM|PM|am|pm))\\\"?", "hh:mm:ss a");

                        // Format HH:mm:ss
                        replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2}:\\d{1,2})\\\"?", "HH:mm:ss");

                        // Format hh:mma
                        replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2}(?:AM|PM|am|pm))\\\"?", "hh:mma");

                        // Format hh:mm a
                        replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2}\\s+(?:AM|PM|am|pm))\\\"?", "hh:mm a");

                        // Format HH:mm
                        replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2})\\\"?", "HH:mm");

                        query = query.replace(clause, replace);

                        break;
                    }
                }
            }

            matches = RegEX.find(query, "\\[([0-9]*)(\\*+) to ");
            for (RegExMatch regExMatch : matches) {
                query = query.replace("[" + regExMatch.getGroups().get(0).getMatch() + regExMatch.getGroups().get(1).getMatch() + " to ", "["
                        + regExMatch.getGroups().get(0).getMatch() + " to ");
            }

            matches = RegEX.find(query, " to ([0-9]*)(\\*+)\\]");
            for (RegExMatch regExMatch : matches) {
                query = query.replace(" to " + regExMatch.getGroups().get(0).getMatch() + regExMatch.getGroups().get(1).getMatch() + "]", " to "
                        + regExMatch.getGroups().get(0).getMatch() + "]");
            }

            matches = RegEX.find(query, "\\[([0-9]*) (to) ([0-9]*)\\]");
            if(matches.isEmpty()){
            	matches = RegEX.find(query, "\\[([a-z0-9]*) (to) ([a-z0-9]*)\\]");
            }
            for (RegExMatch regExMatch : matches) {
                query = query.replace("[" + regExMatch.getGroups().get(0).getMatch() + " to "
                        + regExMatch.getGroups().get(2).getMatch() + "]", "["
                        + regExMatch.getGroups().get(0).getMatch() + " TO " + regExMatch.getGroups().get(2).getMatch()
                        + "]");
            }

            //https://github.com/elasticsearch/elasticsearch/issues/2980
            if (query.contains( "/" )) {
                query = query.replaceAll( "/", "\\\\/" );
            }

            return query;
        }
}
