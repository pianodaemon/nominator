package com.immortalcrab.nominator.mod;

import com.google.inject.AbstractModule;
import com.immortalcrab.nominator.dal.dao.NominatorDao;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NominatorModule extends AbstractModule {

    protected NominatorDao _dao;

    @Override
    protected void configure() {
        bind(NominatorDao.class).toInstance(_dao);
    }
}