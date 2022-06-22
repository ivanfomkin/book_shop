package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.entity.tag.TagWithWeightObject;

import java.util.List;

public interface TagService {
    List<TagWithWeightObject> getTagsWithWeight();
}
