package com.billing.testtask.service;

import com.billing.testtask.model.TagModel;

/**
 * Доменный сервис справочника тегов
 */
public interface TagService {
    /**
     * Сохранениe тега
     * @param tag модель данных тега
     * @return идентификатор сохраненного тега
     */
    TagModel save(TagModel tag);

    /**
     * Удаление тега по идентификатору (вместе с его задачами)
     * @param id идентификатор тега
     */
    void delete(Long id);

    /**
     * Получения данных о теге со всеми его задачами
     *
     * @param id идентификатор тега
     * @return модель данных тега
     */
    TagModel getAllTaskByTag(Long id);
}
