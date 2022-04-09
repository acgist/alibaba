package ${modulePackage}${module}.model.vo;
<#if hasOtherType>

<#if hasBigDecimal>
import java.math.BigDecimal;
</#if>
<#if hasDate>
import java.util.Date;
</#if>
</#if>

import com.acgist.boot.model.BootVo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * ${name}
 * 
 * @author ${author}
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ${prefix}Vo extends BootVo {

	private static final long serialVersionUID = 1L;

	<#list columns as column>
	/**
	 * ${column.comment}
	 */
	private ${column.type} ${column.value};
	</#list>
	
}
