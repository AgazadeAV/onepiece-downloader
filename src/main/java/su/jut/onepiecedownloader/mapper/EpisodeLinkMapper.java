package su.jut.onepiecedownloader.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import su.jut.onepiecedownloader.dto.EpisodeLinkDto;
import su.jut.onepiecedownloader.model.EpisodeLink;

@Mapper
public interface EpisodeLinkMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    EpisodeLink toEntity(EpisodeLinkDto dto);
}
