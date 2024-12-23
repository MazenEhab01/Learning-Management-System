//package org.software.lms.controller;
//
//import org.software.lms.model.LessonResource;
//import org.software.lms.service.LessonResourceService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/resources")
//public class LessonResourceController {
//
//    @Autowired
//    private LessonResourceService lessonResourceService;
//
//    @GetMapping
//    public List<LessonResource> getAllResources() {
//        return lessonResourceService.getAllResources();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<LessonResource> getResourceById(@PathVariable Long id) {
//        Optional<LessonResource> resource = lessonResourceService.getResourceById(id);
//        return resource.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//        @PostMapping
//    public LessonResource createResource(@RequestBody LessonResource resource) {
//        return lessonResourceService.saveResource(resource);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<LessonResource> updateResource(@PathVariable Long id, @RequestBody LessonResource resource) {
//        if (lessonResourceService.getResourceById(id).isPresent()) {
//            resource.setId(id);
//            return ResponseEntity.ok(lessonResourceService.saveResource(resource));
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
//        if (lessonResourceService.getResourceById(id).isPresent()) {
//            lessonResourceService.deleteResource(id);
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.notFound().build();
//    }
//}
