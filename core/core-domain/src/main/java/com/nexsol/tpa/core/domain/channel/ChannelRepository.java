package com.nexsol.tpa.core.domain.channel;

import java.util.List;

/**
 * 채널 Repository 인터페이스 - Business Layer에서 정의하고 Data Access Layer에서 구현
 */
public interface ChannelRepository {

    List<Channel> findAllActive();

    List<Channel> findByPartnerIdAndActive(Long partnerId);

}
