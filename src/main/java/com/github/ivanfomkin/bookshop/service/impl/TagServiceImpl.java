package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.entity.tag.TagWithWeightObject;
import com.github.ivanfomkin.bookshop.repository.TagRepository;
import com.github.ivanfomkin.bookshop.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<TagWithWeightObject> getTagsWithWeight() {
        return tagRepository.findTagsWithWeight();
    }

    @Override
    public List<String> getAllTags() {
        return tagRepository.findAllTagNames();
    }

}
