package dev.nmarulo.depensaapp.commons.service;

import dev.nmarulo.depensaapp.commons.classes.PagingAndSortingRes;
import dev.nmarulo.depensaapp.commons.component.DataRequestScope;
import dev.nmarulo.depensaapp.commons.component.LocalMessage;
import dev.nmarulo.depensaapp.commons.exception.NotFoundException;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

@Getter
public abstract class CrudServiceImp<I, O, E, ID> implements CrudService<I, O, ID> {
    
    private final ModelMapper modelMapper = new ModelMapper();
    
    @Autowired
    private DataRequestScope dataRequestScope;
    
    @Autowired
    private LocalMessage localMessage;
    
    protected abstract JpaRepository<E, ID> getRepository();
    
    @Override
    public PagingAndSortingRes<O> findAll() {
        Type typeO = new TypeToken<PagingAndSortingRes<O>>() {}.getType();
        Page<E> page = getRepository().findAll(this.dataRequestScope.getPageable());
        return modelMapper.map(page, typeO);
    }
    
    @Override
    public O findById(ID id) {
        Type typeO = new TypeToken<O>() {}.getType();
        E entity = getRepository().findById(id).orElse(null);
        return modelMapper.map(entity, typeO);
    }
    
    @Override
    public O save(I request) {
        return saveOrUpdate(null, request);
    }
    
    @Override
    public O update(ID id, I request) {
        checkIsExistById(id);
        return saveOrUpdate(id, request);
    }
    
    @Override
    public void delete(ID id) {
        checkIsExistById(id);
        getRepository().deleteById(id);
    }
    
    private O saveOrUpdate(ID id, I request) {
        Type typeO = new TypeToken<O>() {}.getType();
        Type typeE = new TypeToken<E>() {}.getType();
        
        E entity = modelMapper.map(request, typeE);
        setFieldId(id, entity);
        E save = getRepository().save(entity);
        
        return modelMapper.map(save, typeO);
    }
    
    private void checkIsExistById(ID id) {
        if (!getRepository().existsById(id)) {
            throw new NotFoundException(this.localMessage.getMessage("error.record-not-exist"));
        }
    }
    
    private void setFieldId(ID id, E entity) {
        Field field = ReflectionUtils.findField(entity.getClass(), "id");
        
        if (field != null) {
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, entity, id);
        }
    }
}
