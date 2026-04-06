class query {
public Object query(String query) throws ApplicationException {
        StringBuffer html = new StringBuffer();
        String[] keywords;

        int page = 1, pageSize = 20;

        this.request = (Request) this.context
                .getAttribute(HTTP_REQUEST);
        if (this.request.getParameter("page") == null
                || this.request.getParameter("page").toString().trim().length() <= 0) {
            page = 1;
        } else {
            page = Integer.parseInt(this.request.getParameter("page").toString());
        }

        int startIndex = (page - 1) * pageSize;
        this.setVariable("search.title", "无相关结果 - ");

        if (query.trim().length() > 0) {
            query = StringUtilities.htmlSpecialChars(query);
            if (query.indexOf('|') != -1) {
                String[] q = query.split("|");
                query = q[0];
            }

            query = query.trim();
            keywords = query.split(" ");

            this.setVariable("keyword", query);
            this.setVariable("search.title", query + " - ");
        } else {
            this.setVariable("keyword", "");
            return this;
        }

        StringBuilder condition = new StringBuilder();
        int i = 0, j, k = 0;
        String[] _keywords = new String[keywords.length];
        while (i < keywords.length) {
            _keywords[i] = "%" + keywords[i] + "%";
            if (condition.length() == 0) {
                condition.append(" bible.content like ? ");
            } else {
                condition.append(" AND bible.content like ? ");
            }
            i++;
        }

        Locale locale = this.getLocale();
        if (condition.length() == 0)
            condition.append(" book.language='").append(locale).append("' ");
        else
            condition.append(" AND book.language='").append(locale).append("' ");

        book book = new book();
        bible bible = new bible();
        if (locale.toString().equalsIgnoreCase(Locale.US.toString())) {
            bible.setTableName("NIV");
        } else if (locale.toString().equalsIgnoreCase(Locale.UK.toString())) {
            bible.setTableName("ESV");
        } else {
            bible.setTableName(locale.toString());
        }

        String SQL = "SELECT bible.*,book.book_name FROM " + bible.getTableName()
                + " as bible left join " + book.getTableName()
                + " as book on bible.book_id=book.book_id where " + condition
                + " order by bible.book_id,bible.chapter_id limit " + startIndex + ","
                + pageSize;
        String look = "SELECT count(bible.id) AS size FROM " + bible.getTableName()
                + " as bible left join " + book.getTableName()
                + " as book on bible.book_id=book.book_id where " + condition;

        Table vtable = bible.find(SQL, _keywords);
        boolean noResult = vtable.size() > 0;

        if (!noResult && query.length() > 0) {
            try {
                Table list = book.findWith("WHERE language=? and book_name=?",
                        new Object[]{this.getLocale().toString(), query});
                if (list.size() > 0) {
                    this.response = (Response) this.context
                            .getAttribute(HTTP_RESPONSE);

                    Reforward reforward = new Reforward(request, response);
                    query = URLEncoder.encode(query, "utf-8");
                    reforward.setDefault(this.context.getAttribute("HTTP_HOST") + query);
                    reforward.forward();
                    return reforward;
                }
            } catch (ApplicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Row found = bible.findOne(look, _keywords);

        long startTime = System.currentTimeMillis();
        Pager pager = new Pager();
        pager.setPageSize(pageSize);
        pager.setCurrentPage(page);
        pager.setListSize(found.getFieldInfo("size").intValue());

        Field field;
        int next = pager.getStartIndex();// 此位置即为当前页的第一条记录的ID

        html.append("<ol class=\"searchresults\" start=\"").append(next).append("\">\r\n");

        String finded, word;
        Row row;
        Enumeration<Row> table = vtable.elements();
        int n = 0;
        while (table.hasMoreElements()) {
            row = table.nextElement();
            Iterator<Field> iterator = row.iterator();

            n++;
            while (iterator.hasNext()) {
                field = iterator.next();
                finded = field.get("content").value().toString();

                j = 0;
                while (j < keywords.length) {
                    finded = StringUtilities.sign(finded, keywords[j++]);
                }

                html.append("<li"
                        + (n % 2 == 0 ? " class=\"even\"" : " class=\"odd\"")
                        + "><a href=\""
                        + this.context.getAttribute("HTTP_HOST")
                        + "bible/"
                        + field.get("book_id").value().toString()
                        + "/"
                        + field.get("chapter_id").value().toString()
                        + "/"
                        + field.get("part_id").value().toString()
                        + "\" target=\"_blank\">"
                        + this.setText("search.bible.info", field.get("book_name").value()
                        .toString(), field.get("chapter_id").value().toString(), field
                        .get("part_id").value().toString()) + "</a><p>" + finded
                        + "</p></li> \r\n");
                next++;
            }
        }

        Table ktable;
        Row krow;
        while (k < keywords.length && noResult) {
            word = keywords[k++];
            keyword keyword = new keyword();
            keyword.setKeyword(word);
            ktable = keyword.setRequestFields("id,visit").findWith("WHERE keyword=?",
                    new Object[]{word});

            if (ktable.size() == 0) {
                keyword.setVisit(0);
                keyword.append();
            } else {
                krow = ktable.get(0);
                keyword.setId(krow.getFieldInfo("id").value());
                keyword.setVisit(krow.getFieldInfo("visit").intValue() + 1);
                keyword.update();
            }
        }
        html.append("</ol>\r\n");

        String actionURL = this.context.getAttribute("HTTP_HOST") + "bible/search/"
                + query + "&page";
        pager.setFirstPageText(this.getProperty("page.first.text"));
        pager.setLastPageText(this.getProperty("page.last.text"));
        pager.setCurrentPageText(this.getProperty("page.current.text"));
        pager.setNextPageText(this.getProperty("page.next.text"));
        pager.setEndPageText(this.getProperty("page.end.text"));
        pager.setControlBarText(this.getProperty("page.controlbar.text"));

        html.append("<div class=\"pagination\" style=\"cursor:default\">"
                + pager.getPageControlBar(actionURL) + "</div>\r\n");
        html.append("<!-- "
                + (System.currentTimeMillis() - startTime) + " -->");

        int start = page - 1 == 0 ? 1 : (page - 1) * pageSize + 1, end = page
                * pageSize;

        this.setVariable("start", String.valueOf(start));
        this.setVariable("end", String.valueOf(end));
        this.setVariable("size", String.valueOf(pager.getSize()));
        this.setVariable("value", html.toString());
        this.setVariable("action", this.config.get("default.base_url")
                + this.context.getAttribute("REQUEST_ACTION").toString());

        this.setText("search.info", start, end, query, pager.getSize());

        Session session = request.getSession();
        if (session.getAttribute("usr") != null) {
            this.usr = (User) session.getAttribute("usr");

            this.setVariable("user.status", "");
            this.setVariable("user.profile",
                    "<a href=\"javascript:void(0)\" onmousedown=\"profileMenu.show(event,'1')\">"
                            + this.usr.getEmail() + "</a>");
        } else {
            this.setVariable("user.status", "<a href=\"" + this.getLink("user/login")
                    + "\">" + this.getProperty("page.login.caption") + "</a>");
            this.setVariable("user.profile", "");
        }

        return this;
    }
}
