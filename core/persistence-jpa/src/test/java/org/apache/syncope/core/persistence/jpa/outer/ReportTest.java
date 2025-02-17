/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.persistence.jpa.outer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityExistsException;
import jakarta.ws.rs.core.MediaType;
import java.time.OffsetDateTime;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.dao.ReportDAO;
import org.apache.syncope.core.persistence.api.dao.ReportExecDAO;
import org.apache.syncope.core.persistence.api.entity.Report;
import org.apache.syncope.core.persistence.api.entity.ReportExec;
import org.apache.syncope.core.persistence.jpa.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional("Master")
public class ReportTest extends AbstractTest {

    @Autowired
    private ReportDAO reportDAO;

    @Autowired
    private ReportExecDAO reportExecDAO;

    @Autowired
    private ImplementationDAO implementationDAO;

    @Test
    public void find() {
        Report report = reportDAO.find("0062ea9c-924d-4ecf-9961-4492a8cc6d1b");
        assertNotNull(report);

        assertNotNull(report.getExecs());
        assertFalse(report.getExecs().isEmpty());
        assertEquals(1, report.getExecs().size());
    }

    @Test
    public void saveWithExistingName() {
        assertThrows(EntityExistsException.class, () -> {
            Report report = reportDAO.find("0062ea9c-924d-4ecf-9961-4492a8cc6d1b");
            assertNotNull(report);

            String name = report.getName();

            report = entityFactory.newEntity(Report.class);
            report.setName(name);
            report.setJobDelegate(implementationDAO.find("SampleReportJobDelegate"));
            report.setMimeType(MediaType.TEXT_PLAIN);
            report.setFileExt("txt");
            report.setActive(true);

            reportDAO.save(report);
            entityManager().flush();
        });
    }

    @Test
    public void save() {
        Report report = reportDAO.find("0062ea9c-924d-4ecf-9961-4492a8cc6d1b");
        assertNotNull(report);
        assertEquals(1, report.getExecs().size());

        ReportExec reportExec = entityFactory.newEntity(ReportExec.class);
        reportExec.setReport(report);
        reportExec.setStart(OffsetDateTime.now());
        reportExec.setEnd(OffsetDateTime.now());
        reportExec.setStatus("SUCCESS");
        reportExec.setExecutor("admin");

        report.add(reportExec);
        reportDAO.save(report);

        entityManager().flush();

        report = reportDAO.find("0062ea9c-924d-4ecf-9961-4492a8cc6d1b");
        assertNotNull(report);
        assertEquals(2, report.getExecs().size());
    }

    @Test
    public void deleteReport() {
        reportDAO.delete("0062ea9c-924d-4ecf-9961-4492a8cc6d1b");

        entityManager().flush();

        assertNull(reportDAO.find("0062ea9c-924d-4ecf-9961-4492a8cc6d1b"));
        assertNull(reportExecDAO.find("0062ea9c-924d-4ecf-9961-4492a8cc6d1b"));
    }

    @Test
    public void deleteReportExecution() {
        ReportExec execution = reportExecDAO.find("c13f39c5-0d35-4bff-ba79-3cd5de940369");
        int executionNumber = execution.getReport().getExecs().size();

        reportExecDAO.delete("c13f39c5-0d35-4bff-ba79-3cd5de940369");

        entityManager().flush();

        assertNull(reportExecDAO.find("0062ea9c-924d-4ecf-9961-4492a8cc6d1b"));

        Report report = reportDAO.find("0062ea9c-924d-4ecf-9961-4492a8cc6d1b");
        assertEquals(report.getExecs().size(), executionNumber - 1);
    }
}
