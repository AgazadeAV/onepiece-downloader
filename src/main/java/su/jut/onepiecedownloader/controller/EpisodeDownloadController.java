package su.jut.onepiecedownloader.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import su.jut.onepiecedownloader.dto.DownloadResponseDto;
import su.jut.onepiecedownloader.service.EpisodeDownloadService;
import su.jut.onepiecedownloader.swagger.EpisodeDownloadApiSpec;

@RestController
@RequestMapping("/episodes")
@RequiredArgsConstructor
public class EpisodeDownloadController implements EpisodeDownloadApiSpec {

    private final EpisodeDownloadService episodeDownloadService;

    @GetMapping("/{episodeNumber}")
    public ResponseEntity<DownloadResponseDto> downloadOne(@PathVariable int episodeNumber,
                                                           @RequestParam(defaultValue = "360") String quality) {
        return ResponseEntity.ok(episodeDownloadService.downloadOne(episodeNumber, quality));
    }

    @GetMapping("/range")
    public ResponseEntity<DownloadResponseDto> downloadRange(@RequestParam int start,
                                                             @RequestParam int end,
                                                             @RequestParam(defaultValue = "360") String quality) {
        return ResponseEntity.ok(episodeDownloadService.downloadRange(start, end, quality));
    }

    @GetMapping("/all")
    public ResponseEntity<DownloadResponseDto> downloadAll(@RequestParam(defaultValue = "360") String quality) {
        return ResponseEntity.ok(episodeDownloadService.downloadAll(quality));
    }

    @GetMapping("/available")
    public ResponseEntity<DownloadResponseDto> getAvailableEpisodes() {
        return ResponseEntity.ok(episodeDownloadService.getAvailableEpisodeCount());
    }
}
