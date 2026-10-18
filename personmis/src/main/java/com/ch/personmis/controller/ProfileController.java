package com.ch.personmis.controller;

import com.ch.personmis.service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Map;

@RestController
public class ProfileController {

    @Resource
    private ProfileService profileService;

    @GetMapping("/api/profile/overall")
    public Map<String, Object> getOverallProfile(@RequestParam Integer staffId) {
        return profileService.getOverallProfile(staffId);
    }
}

