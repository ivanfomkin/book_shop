package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.entity.tag.TagWithWeightObject;
import com.example.MyBookShopApp.repository.TagRepository;
import com.example.MyBookShopApp.service.TagService;
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
}
