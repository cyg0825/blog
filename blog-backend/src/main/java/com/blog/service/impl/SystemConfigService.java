package com.blog.service.impl;

import com.blog.entity.SystemConfig;
import com.blog.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigRepository configRepository;

    public List<SystemConfig> getAll() {
        return configRepository.findAll();
    }

    public Optional<String> getConfigValue(String key) {
        return configRepository.findByConfigKey(key).map(SystemConfig::getConfigValue);
    }

    @Transactional
    public SystemConfig saveConfig(String key, String value, String description) {
        SystemConfig config = configRepository.findByConfigKey(key).orElse(new SystemConfig());
        config.setConfigKey(key);
        config.setConfigValue(value);
        if (description != null) config.setDescription(description);
        return configRepository.save(config);
    }

    @Transactional
    public SystemConfig updateConfig(Long id, SystemConfig updated) {
        SystemConfig config = configRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("配置不存在"));
        config.setConfigValue(updated.getConfigValue());
        if (updated.getDescription() != null) config.setDescription(updated.getDescription());
        return configRepository.save(config);
    }

    @Transactional
    public void deleteConfig(Long id) {
        configRepository.deleteById(id);
    }

    @Transactional
    public SystemConfig createConfig(SystemConfig config) {
        if (configRepository.findByConfigKey(config.getConfigKey()).isPresent()) {
            throw new RuntimeException("配置键已存在");
        }
        return configRepository.save(config);
    }
}
