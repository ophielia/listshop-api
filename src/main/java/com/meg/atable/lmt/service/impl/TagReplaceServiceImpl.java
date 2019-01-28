package com.meg.atable.lmt.service.impl;

import com.meg.atable.lmt.service.TagReplaceService;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional
public class TagReplaceServiceImpl implements TagReplaceService {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
    public void replaceTag(Long toReplaceId, Long replaceWithId) {
        Session session = entityManager.unwrap(Session.class);

        replaceTagInDish(toReplaceId,replaceWithId);
        replaceTagInLists(toReplaceId, replaceWithId);
        // clear session
        session.flush();
        session.clear();

    }

    private void replaceTagInLists(Long toReplaceId, Long replaceWithId) {

        // write sql for delete
        StringBuilder baseSql = new StringBuilder();
        StringBuilder withSql = new StringBuilder("with tag_replace as " +
                "                    ( " +
                "                            select " +
                "                            dt.list_id, " +
                "                            count(distinct dt.tag_id) as tag_count " +
                "                            from " +
                "                            list_item dt " +
                "                            where " +
                "                            dt.tag_id in " +
                "                                    ( ");
        withSql.append(toReplaceId).append(",").append(replaceWithId).append(") ");
        withSql.append("                     and list_id is not null " +
                "            group by " +
                "            dt.list_id " +
                ")");

        baseSql.append(withSql);
        baseSql.append("delete " +
                "                    from " +
                "            list_item dt  " +
                "            using tag_replace tr " +
                "                    where " +
                "            tr.list_id = dt.list_id " +
                "            and dt.tag_id = ");
        baseSql.append(toReplaceId).append(" ");
        baseSql.append( "            and tr.tag_count = 2; ");

        // execute sql for delete
        Query query = entityManager.createNativeQuery(baseSql.toString());
        query.executeUpdate();

        // write sql for replace
        baseSql = new StringBuilder();
        baseSql.append(withSql);
        baseSql.append("update list_item dt " +
                "set tag_id = ");
        baseSql.append(replaceWithId).append(" ");
        baseSql.append("from tag_replace tr " +
                "where tr.list_id = dt.list_id " +
                "and dt.tag_id = ");
        baseSql.append(toReplaceId).append(" ");
        baseSql.append("and tr.tag_count = 1;");
        // execute sql for replace
        query = entityManager.createNativeQuery(baseSql.toString());
        query.executeUpdate();
    }

        private void replaceTagInDish(Long toReplaceId, Long replaceWithId) {
            // MM add logging
            // write sql for delete

            StringBuilder baseSql = new StringBuilder();
            StringBuilder withSql = new StringBuilder("with tag_replace as " +
                    "(select dt.dish_id , count(distinct dt.tag_id) as tag_count " +
                    "from dish_tags dt  " +
                    "where dt.tag_id in (");
            withSql.append(toReplaceId).append(",").append(replaceWithId).append(") ");
            withSql.append("group by dt.dish_id) ");
            baseSql.append(withSql);
            baseSql.append("delete  " +
                    "from dish_tags dt " +
                    "using tag_replace tr  " +
                    "where tr.dish_id = dt.dish_id " +
                    "and dt.tag_id = ");
            baseSql.append(toReplaceId).append(" ");
            baseSql.append("and tr.tag_count = 2;");

            // execute sql for delete
            Query query = entityManager.createNativeQuery(baseSql.toString());
            query.executeUpdate();

            // write sql for replace
            baseSql = new StringBuilder();
            baseSql.append(withSql);
            baseSql.append("update dish_tags dt " +
                    "set tag_id = ");
            baseSql.append(replaceWithId).append(" ");
            baseSql.append("from tag_replace tr " +
                    "where tr.dish_id = dt.dish_id " +
                    "and dt.tag_id = ");
            baseSql.append(toReplaceId).append(" ");
            baseSql.append("and tr.tag_count = 1;");
            // execute sql for replace
            query = entityManager.createNativeQuery(baseSql.toString());
            query.executeUpdate();


        }




}
