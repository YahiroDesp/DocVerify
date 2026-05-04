package kz.docverify.dto.mapper;

import kz.docverify.domain.Document;
import kz.docverify.dto.DocumentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    DocumentDto toDto(Document document);

    @Mapping(target = "owner", ignore = true)
    Document toEntity(DocumentDto dto);
}
