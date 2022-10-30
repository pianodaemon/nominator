package com.immortalcrab.nominator.mod;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.immortalcrab.nominator.dal.dao.NominatorDao;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NominatorModule extends AbstractModule {

    protected Class<? extends NominatorDao> _dao;

    @Override
    protected void configure() {
        bind(NominatorDao.class).annotatedWith(
            Names.named("DefaultDao")).to(_dao);
    }
}
