package com.example.MyBookShopApp.data;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TagService {

    private TagRepository tagRepository;
    private Long maxCountBooksWithTag;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
        this.maxCountBooksWithTag = tagRepository.getMaxBooksCount();
    }

    public Map<Tag, String> getTagsMapWithClass() {
        List<Tag> tagsList = tagRepository.findTagByBooksIsNotNullOrderById();
        return tagsList.stream()
                .collect(Collectors.toMap(x -> x,
                        x -> getTagClassByCategory(getTagCategory(x.getBooks().size()))));
    }

    public TagCategoryEnum getTagCategory(int count) {
        Double part = (double) count / maxCountBooksWithTag;
        if (part > 0 && part <= TagCategoryEnum.XS.getPercent()) return TagCategoryEnum.XS;
        else if (part <= TagCategoryEnum.SM.getPercent()) return TagCategoryEnum.SM;
        else if (part <= TagCategoryEnum.MD.getPercent()) return TagCategoryEnum.MD;
        else if (part <= TagCategoryEnum.LG.getPercent()) return TagCategoryEnum.LG;
        else return null;
    }

    public String getTagClassByCategory(TagCategoryEnum category) {
        return category == null ? "" : "Tag_" + category.toString().toLowerCase();
    }
}