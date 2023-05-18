package com.sniff.location.service;

import com.sniff.mapper.LocationMapper;
import com.sniff.location.model.response.Location;
import com.sniff.location.repository.CityRepository;
import com.sniff.location.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final LocationMapper locationMapper;

    public List<Location> getRegions() {
        return locationMapper.regionToLocation(regionRepository.findAll());
    }

    public List<Location> getCities() {
        return locationMapper.cityToLocation(cityRepository.findAll());
    }
}
