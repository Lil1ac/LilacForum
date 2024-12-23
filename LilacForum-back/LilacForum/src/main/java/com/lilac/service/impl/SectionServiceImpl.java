package com.lilac.service.impl;

import com.lilac.mapper.PostMapper;
import com.lilac.mapper.SectionMapper;
import com.lilac.pojo.Post;
import com.lilac.pojo.Section;
import com.lilac.service.PostService;
import com.lilac.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SectionServiceImpl implements SectionService {

    @Autowired
    private SectionMapper sectionMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private PostMapper postMapper;


    @Override
    public List<Section> getAllSections() {
        return sectionMapper.getAllSections();
    }
    @Override
    public Section getSectionById(Integer id) {
        return sectionMapper.getSectionById(id);
    }
    @Override
    public void insertSection(Section section) {
        sectionMapper.insertSection(section);
    }
    @Override
    public void updateSection(Section section) {
        sectionMapper.updateSection(section);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSectionById(Integer sectionId) {
        List<Post> posts = postMapper.getPostsBySectionId(sectionId);
        for (Post post : posts) {
            postService.deletePost(post.getId());
        }
        sectionMapper.deleteSection(sectionId);
    }
}
