package dev.nmarulo.depensaapp.app.unitytypes;

import dev.nmarulo.depensaapp.app.unitytypes.classes.UnitTypeReq;
import dev.nmarulo.depensaapp.app.unitytypes.classes.UnitTypeRes;
import dev.nmarulo.depensaapp.commons.service.CrudServiceImp;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import dev.nmarulo.depensaapp.app.unitytypes.mapper.UnitTypeMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class UnitTypeService extends CrudServiceImp<UnitTypeReq, UnitTypeRes, UnitType, Integer> {
    
    private final UnitTypeRepository repository;
    private final UnitTypeMapper mapper;
    
    @Override
    protected UnitTypeRes convertResponseTo(UnitType entity) {
        return mapper.toUnitTypeRes(entity);
    }
    
    @Override
    protected UnitType convertRequestTo(UnitTypeReq request) {
        return mapper.toUnitType(request);
    }
    
    @Override
    protected List<UnitTypeRes> convertPageTo(List<UnitType> page) {
        return mapper.toUnitTypeResCollection(page);
    }
    
}
