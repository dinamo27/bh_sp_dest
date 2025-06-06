

package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResetProjectController {

    @Autowired
    private ResetProjectService resetProjectService;

    @PostMapping("/reset-project")
    public ResponseEntity<String> resetProject(@RequestBody ProjectId projectId) {
        try {
            int successFlag = resetProjectService.resetProject(projectId);
            if (successFlag == 0) {
                return ResponseEntity.ok("Project reset successfully");
            } else {
                return ResponseEntity.badRequest().body("Error resetting project: ");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error resetting project: " + e.getMessage());
        }
    }
}