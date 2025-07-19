package org.yechan.dto;

import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.yechan.dto.request.ShowRegisterRequest;
import org.yechan.entity.Seller;
import org.yechan.entity.Show;

@Mapper
public interface ShowEntityConverter {
    ShowEntityConverter CONVERTER = Mappers.getMapper(ShowEntityConverter.class);

    @Mapping(target = "seller", source = "seller")
    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)\
    // hall, phone
    @Mapping(target = "contactPhone", source = "seller.contact")
    Show convert(ShowRegisterRequest request, Seller seller, UUID hall);
}
