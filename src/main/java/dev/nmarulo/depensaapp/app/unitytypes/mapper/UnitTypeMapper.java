package dev.nmarulo.depensaapp.app.unitytypes.mapper;

import dev.nmarulo.depensaapp.app.unitytypes.UnitType;
import dev.nmarulo.depensaapp.app.unitytypes.classes.UnitTypeReq;
import dev.nmarulo.depensaapp.app.unitytypes.classes.UnitTypeRes;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UnitTypeMapper {
    
     UnitTypeRes toUnitTypeRes(UnitType request);
    List<UnitTypeRes> toUnitTypeResCollection(List<UnitType> request);
    UnitType toUnitType(UnitTypeReq request);
    
    
}
