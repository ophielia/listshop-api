package com.meg.atable.lmt.service.impl;

import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.lmt.data.entity.TargetEntity;
import com.meg.atable.lmt.data.entity.TargetSlotEntity;
import com.meg.atable.lmt.data.repository.TargetRepository;
import com.meg.atable.lmt.service.TagReplaceService;
import com.meg.atable.lmt.service.TargetService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional
public class TagReplaceServiceImpl implements TagReplaceService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private TargetService targetService;


    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public void replaceTag(Long toReplaceId, Long replaceWithId) {
        Session session = entityManager.unwrap(Session.class);

        replaceTagInDish(toReplaceId, replaceWithId);
        replaceTagInLists(toReplaceId, replaceWithId);
        replaceTagsInTargets(toReplaceId,replaceWithId);
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
        baseSql.append("            and tr.tag_count = 2; ");

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

    // next - for proposal, replace in proposal_dish, proposal_slot

    private void replaceTagsInTargets(Long toReplaceId, Long replaceWithId) {
        // find candidates
        List<Object> candidatesRaw = findTargetCandidates(toReplaceId);
        List<Long> candidates = candidatesRaw.stream().map(o -> Long.valueOf(String.valueOf(o))).collect(Collectors.toList());
        List<TargetEntity> targetEntities = targetRepository.findAllById(candidates);
        String replaceId = String.valueOf(toReplaceId);
        String replaceWith = String.valueOf(replaceWithId);
        // for each candidate -
        for (TargetEntity target : targetEntities) {
            // get user for target
            UserAccountEntity user = userService.getUserById(target.getUserId());
            for (TargetSlotEntity slot : target.getSlots()) {
                if (!slot.getTagIdsAsList().contains(replaceId)) {
                    continue;
                }
                targetService.deleteTagFromTargetSlot(user.getUsername(), target.getTargetId(), slot.getId(), toReplaceId);
                if (!slot.getTagIdsAsList().contains(replaceWith)) {
                    targetService.addTagToTargetSlot(user.getUsername(), target.getTargetId(), slot.getId(), replaceWithId);
                }
            }
        }
    }

    private List<Object> findTargetCandidates(Long toReplaceId) {
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("select distinct target_id from target_slot where " +
                "        (target_tag_ids like '");
        baseSql.append(toReplaceId).append("%' or " +
                "        target_tag_ids like '%;");
        baseSql.append(toReplaceId).append(";%' or " +
                "        target_tag_ids like '%;");
        baseSql.append(toReplaceId).append(";' or " +
                "        target_tag_ids like '%;");
        baseSql.append(toReplaceId).append("')");
        Query query = entityManager.createNativeQuery(baseSql.toString());
        List<Object> candidates = query.getResultList();
        return candidates;
    }
}
