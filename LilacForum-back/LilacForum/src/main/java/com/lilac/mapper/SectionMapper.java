package com.lilac.mapper;


import com.lilac.pojo.Post;
import com.lilac.pojo.Section;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SectionMapper {


    List<Section> getAllSections();
    Section getSectionById(Integer id);
    void insertSection(Section section);
    void updateSection(Section section);
    void deleteSection(Integer id);

}
