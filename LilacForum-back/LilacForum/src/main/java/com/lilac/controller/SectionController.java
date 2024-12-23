package com.lilac.controller;

import com.lilac.pojo.Result;
import com.lilac.pojo.Section;
import com.lilac.service.SectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sections")
public class SectionController {

    @Autowired
    private SectionService sectionService;

    //获取所有板块
    @GetMapping
    public Result getAllSections() {
        log.info("Get all sections");
        List<Section> sections = sectionService.getAllSections();
        return Result.success(sections);
    }

    //获取具体板块
    @GetMapping("/{sectionId}")
    public Result getSectionById(@PathVariable Integer sectionId) {
        log.info("Get section by id: {}", sectionId);
        Section section = sectionService.getSectionById(sectionId);
        return Result.success(section);
    }

    //新增板块
    @PostMapping
    public Result insertSection(@RequestBody Section section) {
        log.info("Insert section: {}", section);
        sectionService.insertSection(section);
        return Result.success();
    }

    //更新板块
    @PutMapping
    public Result updateSection(@RequestBody Section section) {
        log.info("Update section: {}", section);
        sectionService.updateSection(section);
        return Result.success();
    }

    //删除板块
    @DeleteMapping("/{sectionId}")
    public Result deleteSectionById(@PathVariable Integer sectionId) {
        log.info("Delete section by id: {}", sectionId);
        sectionService.deleteSectionById(sectionId);
        return Result.success();
    }
}
