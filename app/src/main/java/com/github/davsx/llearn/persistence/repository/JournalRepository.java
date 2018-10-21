package com.github.davsx.llearn.persistence.repository;

import com.github.davsx.llearn.persistence.dao.JournalDao;
import com.github.davsx.llearn.persistence.entity.JournalEntity;

import javax.inject.Inject;
import java.util.List;

public class JournalRepository {

    private JournalDao journalDao;

    @Inject
    public JournalRepository(JournalDao journalDao) {
        this.journalDao = journalDao;
    }

    public Long save(JournalEntity journal) {
        return journalDao.save(journal);
    }

    public void saveMany(List<JournalEntity> journals) {
        journalDao.saveMany(journals);
    }

    public Integer allJournalsCount() {
        return journalDao.allJournalsCount();
    }

    public void deleteAllJournals() {
        journalDao.deleteAllJournals();
    }

    public List<JournalEntity> getJournalsChunked(long journalId, int limit) {
        return journalDao.getJournalsChunked(journalId, limit);
    }
}
