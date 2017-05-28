package com.meg.atable.service.impl;

import com.meg.atable.model.Tag;
import com.meg.atable.model.TagRelation;
import com.meg.atable.repository.TagRelationRepository;
import com.meg.atable.repository.TagRepository;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagRelationRepository tagRelationRepository;

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Optional<Tag> getTagById(Long tagId) {
        return Optional.of(tagRepository.findOne(tagId));
    }

    @Override
    public Collection<Tag> getTagList() {
        return tagRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteAll() {
        List<Tag> tags = tagRepository.findAll();

        tagRepository.deleteInBatch(tags);
    }

    @Override
    @Transactional
    public void deleteAllRelationships() {
        tagRelationRepository.deleteAll();
    }

    @Override
    public Tag createTag(Tag parent, String name) {
        return createTag(parent,name, null);

    }

    @Override
    public Tag createTag(Tag parent, String name, String description) {
        Tag newtag = new Tag(name,description);
        newtag = tagRepository.save(newtag);

        TagRelation relation = new TagRelation(parent,newtag);
        tagRelationRepository.save(relation);
        return newtag;
    }
}
