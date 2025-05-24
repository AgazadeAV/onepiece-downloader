package su.jut.onepiecedownloader.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import su.jut.onepiecedownloader.dto.AvailableEpisodesDto;
import su.jut.onepiecedownloader.dto.DownloadOneResponseDto;
import su.jut.onepiecedownloader.dto.DownloadRangeResponseDto;
import su.jut.onepiecedownloader.dto.ScanResultDto;
import su.jut.onepiecedownloader.service.EpisodeManagerService;
import su.jut.onepiecedownloader.swagger.EpisodeDownloadApiSpec;

@RestController
@RequestMapping(EpisodeDownloadController.BASE_PATH)
@RequiredArgsConstructor
public class EpisodeDownloadController implements EpisodeDownloadApiSpec {

    public static final String BASE_PATH = "/episodes";
    public static final String DOWNLOAD_ONE = "/{episodeNumber}";
    public static final String DOWNLOAD_RANGE = "/range";
    public static final String DOWNLOAD_ALL = "/all";
    public static final String AVAILABLE = "/available";
    public static final String SCAN = "/scan";
    public static final String CHECK_NEW_EPISODE = "/check-new-episode";

    private final EpisodeManagerService episodeManagerService;

    @GetMapping(DOWNLOAD_ONE)
    public ResponseEntity<DownloadOneResponseDto> downloadOne(@PathVariable int episodeNumber,
                                                              @RequestParam String quality) {
        return ResponseEntity.ok(episodeManagerService.downloadOne(episodeNumber, quality));
    }

    @GetMapping(DOWNLOAD_RANGE)
    public ResponseEntity<DownloadRangeResponseDto> downloadRange(@RequestParam int start,
                                                                  @RequestParam int end,
                                                                  @RequestParam String quality) {
        return ResponseEntity.ok(episodeManagerService.downloadRange(start, end, quality));
    }

    @GetMapping(DOWNLOAD_ALL)
    public ResponseEntity<DownloadRangeResponseDto> downloadAll(@RequestParam String quality) {
        return ResponseEntity.ok(episodeManagerService.downloadAll(quality));
    }

    @GetMapping(AVAILABLE)
    public ResponseEntity<AvailableEpisodesDto> getAvailableEpisodes() {
        return ResponseEntity.ok(episodeManagerService.getAvailableEpisodeCount());
    }

    @GetMapping(SCAN)
    public ResponseEntity<ScanResultDto> scanAndSaveMissingEpisodes() {
        return ResponseEntity.ok(episodeManagerService.scanAndSaveMissingEpisodes());
    }

    @GetMapping(CHECK_NEW_EPISODE)
    public ResponseEntity<ScanResultDto> checkNewEpisode() {
        return ResponseEntity.ok(episodeManagerService.checkForNewEpisodeAndSave());
    }
}
