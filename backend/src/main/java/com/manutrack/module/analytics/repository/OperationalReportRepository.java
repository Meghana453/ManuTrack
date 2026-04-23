package com.manutrack.module.analytics.repository;

import com.manutrack.module.analytics.entity.OperationalReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationalReportRepository extends JpaRepository<OperationalReport, Long> {}
