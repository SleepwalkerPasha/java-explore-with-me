package ru.practicum.ewm.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.admin.service.AdminsCompilationService;
import ru.practicum.ewm.dto.api.Compilation;
import ru.practicum.ewm.admin.dto.api.NewCompilation;
import ru.practicum.ewm.admin.dto.api.UpdateCompilationRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminsCompilationController {

    private final AdminsCompilationService compilationService;

    @PostMapping(path = "")
    public Compilation createCompilation(@RequestBody @Valid NewCompilation newCompilation) {
        return compilationService.createCompilation(newCompilation);
    }

    @DeleteMapping(path = "/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping(path = "/{compId}")
    public Compilation updateCompilation(@PathVariable Long compId,
                                         @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }

}
