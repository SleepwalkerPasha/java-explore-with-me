package ru.practicum.ewm.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.admin.dto.api.NewUserRequest;
import ru.practicum.ewm.admin.dto.api.UserFull;
import ru.practicum.ewm.admin.service.AdminsUserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminsUserController {

    private final AdminsUserService adminsUserService;

    @GetMapping(path = "")
    public List<UserFull> getUsersInUsersIds(@RequestParam(name = "ids", required = false) String ids,
                                             @RequestParam(name = "int", defaultValue = "0") int from,
                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        return adminsUserService.getUsersInUsersIds(ids, from, size);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public UserFull createUser(@RequestBody @Valid NewUserRequest userRequest) {
        return adminsUserService.createUser(userRequest);
    }

    @DeleteMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        adminsUserService.deleteUser(userId);
    }
}
