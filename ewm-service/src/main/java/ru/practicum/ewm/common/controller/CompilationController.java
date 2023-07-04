package ru.practicum.ewm.common.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.service.CompilationService;
import ru.practicum.ewm.dto.api.Compilation;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<Compilation> getCompilations(@RequestParam(name = "pinned", required = false) boolean pinned,
                                             @RequestParam(name = "from", defaultValue = "0") int from,
                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        return compilationService.getAllCompilations(pinned, from, size);
    }

    @GetMapping(path = "/{compId}")
    public Compilation getCompilationById(@PathVariable long compId) {
        return compilationService.getCompilationById(compId);
    }
}
