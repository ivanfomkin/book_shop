package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.tag.TagWithWeightObject;

import java.util.List;

public interface TagService {
    List<TagWithWeightObject> getTagsWithWeight();
}
