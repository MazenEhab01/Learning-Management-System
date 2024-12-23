package org.software.lms.service;

import org.software.lms.model.LessonResource;
import org.software.lms.repository.LessonResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonResourceService {

    @Autowired
    private LessonResourceRepository lessonResourceRepository;

    public List<LessonResource> getAllResources() {
        return lessonResourceRepository.findAll();
    }

    public Optional<LessonResource> getResourceById(Long id) {
        return lessonResourceRepository.findById(id);
    }

    public LessonResource saveResource(LessonResource resource) {
        return lessonResourceRepository.save(resource);
    }

    public void deleteResource(Long id) {
        lessonResourceRepository.deleteById(id);
    }
}
