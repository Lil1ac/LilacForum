package com.lilac.service;

import com.lilac.pojo.Post;
import com.lilac.pojo.Section;

import java.util.List;

public interface SectionService {
    List<Section> getAllSections();
    Section getSectionById(Integer id);

    void insertSection(Section section);
    void deleteSectionById(Integer id);
    void updateSection(Section section);

}
