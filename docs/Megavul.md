Data Model:

```
MegaVul JSON
│
└── [ CVE Entry ]  ×775
    ├── cve_id                  : str
    ├── cwe_ids                 : list
    ├── description             : str
    ├── publish_date            : str
    ├── last_modify_date        : str
    ├── cvss_vector             : str
    ├── cvss_base_score         : float
    ├── cvss_base_severity      : str
    ├── cvss_is_v3              : bool
    │
    └── commits                 : list
        │
        └── [ Commit ]  ×n
            ├── repo_name               : str
            ├── commit_msg              : str
            ├── commit_hash             : str
            ├── parent_commit_hash      : str
            ├── commit_date             : int (unix)
            ├── raw_file_paths          : list[str]
            ├── git_url                 : str
            │
            └── files                   : list
                │
                └── [ File ]  ×n
                    ├── file_name               : str
                    ├── file_path               : str
                    ├── language                : str
                    │
                    ├── vulnerable_functions    : list
                    │   │
                    │   └── [ Function ]  ×n          ← diff pair (before/after)
                    │       ├── func_name                       : str
                    │       ├── parameter_list_signature_before : str
                    │       ├── parameter_list_before           : list
                    │       ├── return_type_before              : str
                    │       ├── func_before                     : str
                    │       ├── abstract_func_before            : str
                    │       ├── abstract_symbol_table_before    : dict
                    │       ├── func_graph_path_before          : str
                    │       ├── parameter_list_signature_after  : str
                    │       ├── parameter_list_after            : list
                    │       ├── return_type_after               : str
                    │       ├── func_after                      : str
                    │       ├── abstract_func_after             : str
                    │       ├── abstract_symbol_table_after     : dict
                    │       ├── func_graph_path_after           : str
                    │       ├── diff_func                       : str
                    │       └── diff_line_info                  : dict
                    │
                    └── non_vulnerable_functions : list
                        │
                        └── [ Function ]  ×n          ← single snapshot (no diff)
                            ├── func_name                   : str
                            ├── parameter_list_signature    : str
                            ├── parameter_list              : list
                            ├── return_type                 : str
                            ├── func                        : str
                            ├── abstract_func               : str
                            ├── abstract_symbol_table       : dict
                            └── func_graph_path             : str
```

Sample 'Function' (truncated):
```

=== vulnerable_functions[0] field samples ===

func_name (str)
hasPageExtensions

parameter_list_signature_before (str)
(XWikiContext context)

parameter_list_before (list)
len=1 | head=[['XWikiContext', 'context', -1]]

return_type_before (str)
boolean

func_before (str)
@Override\n    public boolean hasPageExtensions(XWikiContext context)\n    {\n        XWikiDocument doc = context.getDoc();\n        if (doc != null) {\n            List<BaseObject> objects = doc.getObjects(getExtensionC...

abstract_func_before (str)
@Override\n    public boolean hasPageExtensions(XWikiContext VAR_0)\n    {\n        XWikiDocument VAR_1 = VAR_0.getDoc();\n        if (VAR_1 != null) {\n            List<BaseObject> VAR_2 = VAR_1.getObjects(getExtensionC...

abstract_symbol_table_before (dict)
keys=['position_map', 'abstract_table'] | sample={'abstract_table': {'ANNOTATION': {'Override': 'ANNOTATION_0'},
                    'CHAR': {},
                    'COMMENT': {},
                    'FIELD': {},
                    'FUNC': {'equals': 'FUNC_4',
                             'getDoc': 'FUNC_0',
                             'getExtensionClassName': 'FUNC_2',
                             'getObjects': 'FUNC_1',
                             'getStringValue': 'FUNC_3'},
                    'LABEL': {},
                    'NUMBER': {},
                    'STR': {'"currentPage"': 'STR_0'},
                    'TYPE': {'BaseObject': 'TYPE_3',
                             'List': 'TYPE_2',
                             'XWikiContext': 'TYPE_0',
                             'XWikiDocument': 'TYPE_1'},
                    'VAR': {'USE_FIELDNAME': 'VAR_4',
                            'context': 'VAR_0',
                            'doc': 'VAR_1',
                            'obj': 'VAR_3',
                            'objects': 'VAR_2'}},
 'position_map': {'0': [[1, -1, 'ANNOTATION', 'Override', False]],
                  '1': [[37, -1, 'TYPE', 'XWikiContext', False],
                        [50, -1, 'VAR', 'context', False]],
                  '11': [[24, -1, 'VAR', 'obj', False],
                         [28, -1, 'FUNC', 'getStringValue', False],
                         [43, -1, 'VAR', 'USE_FIELDNAME', False],
                         [58, -1, 'FUNC', 'equals', False],
                         [65, -1, 'STR', '"currentPage"', False]],
                  '3': [[8, -1, 'TYPE', 'XWikiDocument', False],
                        [22, -1, 'VAR', 'doc', False],
                        [28, -1, 'VAR', 'context', False],
                        [36, -1, 'FUNC', 'getDoc', False]],
                  '4': [[12, -1, 'VAR', 'doc', False]],
                  '5': [[12, -1, 'TYPE', 'List', False],
                        [17, -1, 'TYPE', 'BaseObject', False],
                        [29, -1, 'VAR', 'objects', False],
                        [39, -1, 'VAR', 'doc', False],
                        [43, -1, 'FUNC', 'getObjects', False],
                        [54, -1, 'FUNC', 'getExtensionClassName', False]],
                  '6': [[16, -1, 'VAR', 'objects', False]],
                  '7': [[21, -1, 'TYPE', 'BaseObject', False],
                        [32, -1, 'VAR', 'obj', False],
                        [38, -1, 'VAR', 'objects', False]],
                  '8': [[24, -1, 'VAR', 'obj', False]]}}

func_graph_path_before (str)
xwiki/xwiki-platform/fe65bc35d5672dd2505b7ac4ec42aec57d500fbb/AbstractDocumentSkinExtensionPlugin.java/vul/before/0.json

parameter_list_signature_after (str)
(XWikiContext context)

parameter_list_after (list)
len=1 | head=[['XWikiContext', 'context', -1]]

return_type_after (str)
boolean

func_after (str)
@Override\n    public boolean hasPageExtensions(XWikiContext context)\n    {\n        XWikiDocument doc = context.getDoc();\n        boolean result = false;\n        if (doc != null && this.hasCurrentPageExtensionObjects...

abstract_func_after (str)
@Override\n    public boolean hasPageExtensions(XWikiContext VAR_0)\n    {\n        XWikiDocument VAR_1 = VAR_0.getDoc();\n        boolean VAR_2 = false;\n        if (VAR_1 != null && this.hasCurrentPageExtensionObjects(...

abstract_symbol_table_after (dict)
keys=['position_map', 'abstract_table'] | sample={'abstract_table': {'ANNOTATION': {'Override': 'ANNOTATION_0'},
                    'CHAR': {},
                    'COMMENT': {},
                    'FIELD': {'SCRIPT': 'FIELD_0'},
                    'FUNC': {'displayScriptRightLog': 'FUNC_6',
                             'getAuthorReference': 'FUNC_4',
                             'getAuthorizationManager': 'FUNC_2',
                             'getDoc': 'FUNC_0',
                             'getDocumentReference': 'FUNC_5',
                             'hasAccess': 'FUNC_3',
                             'hasCurrentPageExtensionObjects': 'FUNC_1'},
                    'LABEL': {},
                    'NUMBER': {},
                    'STR': {},
                    'TYPE': {'XWikiContext': 'TYPE_0',
                             'XWikiDocument': 'TYPE_1'},
                    'VAR': {'Right': 'VAR_3',
                            'context': 'VAR_0',
                            'doc': 'VAR_1',
                            'result': 'VAR_2'}},
 'position_map': {'0': [[1, -1, 'ANNOTATION', 'Override', False]],
                  '1': [[37, -1, 'TYPE', 'XWikiContext', False],
                        [50, -1, 'VAR', 'context', False]],
                  '10': [[16, -1, 'FUNC', 'displayScriptRightLog', False],
                         [38, -1, 'VAR', 'doc', False],
                         [42, -1, 'FUNC', 'getDocumentReference', False]],
                  '13': [[15, -1, 'VAR', 'result', False]],
                  '3': [[8, -1, 'TYPE', 'XWikiDocument', False],
                        [22, -1, 'VAR', 'doc', False],
                        [28, -1, 'VAR', 'context', False],
                        [36, -1, 'FUNC', 'getDoc', False]],
                  '4': [[16, -1, 'VAR', 'result', False]],
                  '5': [[12, -1, 'VAR', 'doc', False],
                        [32, -1, 'FUNC', 'hasCurrentPageExtensionObjects',
                         False],
                        [63, -1, 'VAR', 'doc', False]],
                  '6': [[16, -1, 'FUNC', 'getAuthorizationManager', False],
                        [42, -1, 'FUNC', 'hasAccess', False],
                        [52, -1, 'VAR', 'Right', False],
                        [58, -1, 'FIELD', 'SCRIPT', False],
                        [66, -1, 'VAR', 'doc', False],
                        [70, -1, 'FUNC', 'getAuthorReference', False]],
                  '7': [[16, -1, 'VAR', 'doc', False],
                        [20, -1, 'FUNC', 'getDocumentReference', False]],
                  '8': [[16, -1, 'VAR', 'result', False]]}}

func_graph_path_after (str)
xwiki/xwiki-platform/fe65bc35d5672dd2505b7ac4ec42aec57d500fbb/AbstractDocumentSkinExtensionPlugin.java/vul/after/0.json

diff_func (str)
--- func_before\n+++ func_after\n@@ -2,18 +2,14 @@\n     public boolean hasPageExtensions(XWikiContext context)\n     {\n         XWikiDocument doc = context.getDoc();\n-        if (doc != null) {\n-            List<Base...

diff_line_info (dict)
keys=['deleted_lines', 'added_lines'] | sample={'added_lines': ['        boolean result = false;',
                 '        if (doc != null && '
                 'this.hasCurrentPageExtensionObjects(doc)) {',
                 '            if '
                 '(getAuthorizationManager().hasAccess(Right.SCRIPT, '
                 'doc.getAuthorReference(),',
                 '                doc.getDocumentReference())) {',
                 '                result = true;', '            } else {',
                 '                '
                 'displayScriptRightLog(doc.getDocumentReference());',
                 '        return result;'],
 'deleted_lines': ['        if (doc != null) {',
                   '            List<BaseObject> objects = '
                   'doc.getObjects(getExtensionClassName());',
                   '            if (objects != null) {',
                   '                for (BaseObject obj : objects) {',
                   '                    if (obj == null) {',
                   '                        continue;', '                    }',
                   '                    if '
                   '(obj.getStringValue(USE_FIELDNAME).equals("currentPage")) '
                   '{',
                   '                        return true;',
                   '                    }', '                }',
                   '        return false;']}

=== non_vulnerable_functions[0] field samples ===

func_name (str)
onEvent

parameter_list_signature (str)
(Event event,Object source,Object data)

parameter_list (list)
len=3 | head=[['Event', 'event', -1], ['Object', 'source', -1], ['Object', 'data', -1]]

return_type (str)
void

func (str)
@Override\n    public void onEvent(Event event, Object source, Object data)\n    {\n        if (event instanceof WikiDeletedEvent) {\n            this.alwaysUsedExtensions.remove(((WikiDeletedEvent) event).getWikiId());\...

abstract_func (str)
@Override\n    public void onEvent(Event VAR_0, Object VAR_1, Object VAR_2)\n    {\n        if (VAR_0 instanceof WikiDeletedEvent) {\n            this.alwaysUsedExtensions.remove(((WikiDeletedEvent) VAR_0).getWikiId());\...

abstract_symbol_table (dict)
keys=['position_map', 'abstract_table'] | sample={'abstract_table': {'ANNOTATION': {'Override': 'ANNOTATION_0'},
                    'CHAR': {},
                    'COMMENT': {},
                    'FIELD': {'alwaysUsedExtensions': 'FIELD_0'},
                    'FUNC': {'getWikiId': 'FUNC_1',
                             'onDocumentEvent': 'FUNC_2',
                             'remove': 'FUNC_0'},
                    'LABEL': {},
                    'NUMBER': {},
                    'STR': {},
                    'TYPE': {'Event': 'TYPE_0',
                             'Object': 'TYPE_1',
                             'WikiDeletedEvent': 'TYPE_2',
                             'XWikiDocument': 'TYPE_3'},
                    'VAR': {'data': 'VAR_2',
                            'event': 'VAR_0',
                            'source': 'VAR_1'}},
 'position_map': {'0': [[1, -1, 'ANNOTATION', 'Override', False]],
                  '1': [[24, -1, 'TYPE', 'Event', False],
                        [30, -1, 'VAR', 'event', False],
                        [37, -1, 'TYPE', 'Object', False],
                        [44, -1, 'VAR', 'source', False],
                        [52, -1, 'TYPE', 'Object', False],
                        [59, -1, 'VAR', 'data', False]],
                  '3': [[12, -1, 'VAR', 'event', False],
                        [29, -1, 'TYPE', 'WikiDeletedEvent', False]],
                  '4': [[17, -1, 'FIELD', 'alwaysUsedExtensions', False],
                        [38, -1, 'FUNC', 'remove', False],
                        [47, -1, 'TYPE', 'WikiDeletedEvent', False],
                        [65, -1, 'VAR', 'event', False],
                        [72, -1, 'FUNC', 'getWikiId', False]],
                  '6': [[12, -1, 'FUNC', 'onDocumentEvent', False],
                        [29, -1, 'TYPE', 'XWikiDocument', False],
                        [44, -1, 'VAR', 'source', False]]}}

func_graph_path (str)
xwiki/xwiki-platform/fe65bc35d5672dd2505b7ac4ec42aec57d500fbb/AbstractDocumentSkinExtensionPlugin.java/non_vul/0.json
```