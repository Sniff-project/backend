package com.sniff.mapper;

import com.sniff.location.model.entity.City;
import com.sniff.location.model.entity.Region;
import com.sniff.location.model.response.Location;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    List<Location> regionToLocation(List<Region> regions);
    List<Location> cityToLocation(List<City> cities);
}
