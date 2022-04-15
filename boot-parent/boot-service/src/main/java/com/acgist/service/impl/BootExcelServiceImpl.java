package com.acgist.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanMap;

import com.acgist.boot.BeanUtils;
import com.acgist.boot.CollectionUtils;
import com.acgist.boot.MapUtils;
import com.acgist.boot.model.MessageCodeException;
import com.acgist.dao.mapper.BootMapper;
import com.acgist.model.entity.BootEntity;
import com.acgist.service.BootExcelService;
import com.acgist.service.excel.StringFormatter;

import lombok.extern.slf4j.Slf4j;

/**
 * Excel Service实现
 * 
 * @author acgist
 *
 * @param <M> Mapper
 * @param <T> 类型
 */
@Slf4j
public abstract class BootExcelServiceImpl<M extends BootMapper<T>, T extends BootEntity> extends BootServiceImpl<M, T> implements BootExcelService<T> {

	@Value("${system.excel.header.font:宋体}")
	private String headerFont;
	@Value("${system.excel.header.size:16}")
	private Short headerSize;
	@Value("${system.excel.header.font:宋体}")
	private String cellFont;
	@Value("${system.excel.header.size:12}")
	private Short cellSize;
	@Value("${system.excel.font.width:256}")
	private Integer fontWidth;
	
	/**
	 * Header缓存
	 */
	private Map<String, ExcelHeaderValue> header;
	
	@Override
	public void download(List<T> list, Map<String, ExcelHeaderValue> header, OutputStream output) throws IOException {
		try (
			output;
			final XSSFWorkbook workbook = new XSSFWorkbook();
		) {
			final AtomicInteger col = new AtomicInteger(0);
			final AtomicInteger row = new AtomicInteger(0);
			final XSSFSheet sheet = workbook.createSheet(this.entityClass.getSimpleName());
			// 列宽
			final List<Integer> colWidth = new ArrayList<>();
			// 设置列头
			final XSSFRow headerRow = sheet.createRow(row.getAndIncrement());
			final Set<String> keys = header.keySet();
			final List<ExcelHeaderValue> headers = new ArrayList<>(header.values());
			headers.forEach(value -> {
				final XSSFCell cell = headerRow.createCell(col.getAndIncrement());
				cell.setCellStyle(this.headerCellStyle(workbook));
				final String data = value.getName();
				cell.setCellValue(data);
				colWidth.add(data.getBytes().length);
			});
			// 设置数据
			list.forEach(value -> {
				col.set(0);
				final XSSFRow dataRow = sheet.createRow(row.getAndIncrement());
				keys.forEach(field -> {
					Object object = null;
					try {
						object = FieldUtils.getField(value.getClass(), field, true).get(value);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						log.error("读取属性异常：{}", field, e);
					}
					final ExcelHeaderValue excelHeaderValue = headers.get(col.get());
					final XSSFCell cell = dataRow.createCell(col.get());
					cell.setCellStyle(this.dataCellStyle(workbook));
					final String data = excelHeaderValue.getFormatter().format(object);
					cell.setCellValue(data);
					colWidth.set(col.get(), Math.max(colWidth.get(col.get()), data.getBytes().length));
					col.incrementAndGet();
				});
			});
			// 设置宽度
			col.set(0);
			colWidth.forEach(value -> sheet.setColumnWidth(col.getAndIncrement(), this.fontWidth * value));
			workbook.write(output);
		} finally {
			output.flush();
		}
	}
	
	@Override
	public CellStyle headerCellStyle(XSSFWorkbook workbook) {
		final Font font = workbook.createFont();
		font.setFontName(this.headerFont);
		font.setFontHeightInPoints(this.headerSize);
		final CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(font);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		return headerCellStyle;
	}
	
	@Override
	public CellStyle dataCellStyle(XSSFWorkbook workbook) {
		final Font font = workbook.createFont();
		font.setFontName(this.cellFont);
		font.setFontHeightInPoints(this.cellSize);
		final CellStyle dataCellStyle = workbook.createCellStyle();
		dataCellStyle.setFont(font);
		// 设置边框
//		dataCellStyle.setBorderBottom(BorderStyle.THIN);
//		dataCellStyle.setBorderLeft(BorderStyle.THIN);
//		dataCellStyle.setBorderTop(BorderStyle.THIN);
//		dataCellStyle.setBorderRight(BorderStyle.THIN);
		return dataCellStyle;
	}

	@Override
	public Map<String, ExcelHeaderValue> header() {
		if(this.header != null) {
			return this.header;
		}
		// 注解
		this.header = FieldUtils.getAllFieldsList(this.entityClass).stream()
			.map(field -> {
				final ExcelHeader header = field.getAnnotation(ExcelHeader.class);
				if(header == null) {
					return null;
				}
				return Map.entry(
					field.getName(),
					new ExcelHeaderValue(field.getName(), header.name(), header.loadName(), this.formatter(header.formatter()))
				);
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k, v) -> v, LinkedHashMap::new));
		// 属性
		if(MapUtils.isEmpty(this.header)) {
			this.header = FieldUtils.getAllFieldsList(this.entityClass).stream()
				.map(field -> Map.entry(
					field.getName(),
					new ExcelHeaderValue(field.getName(), field.getName(), field.getName(), this.formatter(StringFormatter.class)))
				)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k, v) -> v, LinkedHashMap::new));
		}
		return this.header;
	}
	
	
	@Override
	public List<List<Object>> load(InputStream input, int sheet) {
		final List<List<Object>> list = new ArrayList<>();
		try (
			input;
			final XSSFWorkbook workbook = new XSSFWorkbook(input);
		) {
			final XSSFSheet sheetValue = workbook.getSheetAt(sheet);
			sheetValue.forEach(row -> {
				final List<Object> data = new ArrayList<>();
				row.forEach(cell -> {
					final CellType cellType = cell.getCellType();
					if(cellType == CellType.STRING) {
						data.add(cell.getStringCellValue());
					} else if(cellType == CellType.BOOLEAN) {
						data.add(cell.getBooleanCellValue());
					} else if(cellType == CellType.NUMERIC) {
						data.add(cell.getNumericCellValue());
					} else {
						data.add(cell.getStringCellValue());
					}
				});
				list.add(data);
			});
		} catch (IOException e) {
			throw MessageCodeException.of(e, "读取Excel文件异常");
		}
		return list;
	}

	@Override
	public <K> List<K> load(List<List<Object>> list, Class<K> clazz, Map<String, ExcelHeaderValue> header) {
		if(CollectionUtils.isEmpty(list)) {
			return List.of();
		}
		// 表头
		final List<Object> excelHeader = list.get(0);
		// 表头 = 字段信息
		final Map<String, ExcelHeaderValue> mapping = header.entrySet().stream()
			.collect(Collectors.toMap(entry -> entry.getValue().getLoadName(), entry -> entry.getValue()));
		// 索引 = 字段信息
		final Map<Integer, ExcelHeaderValue> headerMapping = new HashMap<>();
		for (int index = 0; index < excelHeader.size(); index++) {
			headerMapping.put(index, mapping.get(excelHeader.get(index).toString()));
		}
		return list.stream()
			// 跳过表头
			.skip(1)
			// 表头字段转换
			.map(data -> {
				final Map<String, Object> map = new HashMap<>();
				for (int index = 0; index < data.size(); index++) {
					final ExcelHeaderValue headerValue = headerMapping.get(index);
					map.put(headerValue.getField(), headerValue.getFormatter().parse(data.get(index)));
				}
				return map;
			})
			// 数据类型转换
			.map(data -> {
				final K newInstance = BeanUtils.newInstance(clazz);
				if(newInstance == null) {
					return null;
				}
				BeanMap.create(newInstance).putAll(data);
				return newInstance;
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}
	
}