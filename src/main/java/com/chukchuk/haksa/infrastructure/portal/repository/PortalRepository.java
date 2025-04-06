package com.chukchuk.haksa.infrastructure.portal.repository;

import com.chukchuk.haksa.infrastructure.portal.client.PortalClient;
import com.chukchuk.haksa.infrastructure.portal.dto.raw.RawPortalData;
import com.chukchuk.haksa.infrastructure.portal.mapper.PortalDataMapper;
import com.chukchuk.haksa.infrastructure.portal.model.PortalData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PortalRepository {
    private final PortalClient portalClient;

    public void login(String username, String password) {
        portalClient.login(username, password);
    }

    public PortalData fetchPortalData(String username, String password) {
        RawPortalData rawPortalData = portalClient.scrapeAll(username, password);
        return PortalDataMapper.toPortalData(rawPortalData);
    }
}
