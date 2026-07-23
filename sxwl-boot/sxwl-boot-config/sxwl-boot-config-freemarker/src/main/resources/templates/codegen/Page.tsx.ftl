import React, { useState, useEffect, useCallback } from 'react';
import { SxwlPage, SxwlSearchForm, SxwlFormModal, SxwlButton, SxwlInput, SxwlSelect } from '@/components';
import type { ColumnsType } from '@/types/sxwl';
import {
    get${bizNamePlural}Page,
    get${bizName}ById,
    create${bizName},
    update${bizName},
    delete${bizName},
    ${bizName}DTO,
    ${bizName}PageParams,
} from '@/api/${modulePrefix}/${bizNameLower}Api';
import { message, Switch } from 'antd';

const ${bizName}Page: React.FC = () => {
    const [dataSource, setDataSource] = useState<${bizName}DTO[]>([]);
    const [loading, setLoading] = useState(false);
    const [total, setTotal] = useState(0);
    const [current, setCurrent] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [searchParams, setSearchParams] = useState<${bizName}PageParams>({ current: 1, pageSize: 10 });
    const [modalVisible, setModalVisible] = useState(false);
    const [editingId, setEditingId] = useState<number | undefined>();
    const [confirmLoading, setConfirmLoading] = useState(false);

    const fetchData = useCallback(async (params: ${bizName}PageParams) => {
        setLoading(true);
        try {
            const res = await get${bizNamePlural}Page(params);
            setDataSource(res.data.rows);
            setTotal(res.data.total);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchData(searchParams);
    }, [fetchData, searchParams]);

    const handleSearch = (values: ${bizName}PageParams) => {
        const params = { ...searchParams, ...values, current: 1 };
        setCurrent(1);
        setSearchParams(params);
    };

    const handlePageChange = (page: number, size: number) => {
        setCurrent(page);
        setPageSize(size);
        setSearchParams(prev => ({ ...prev, current: page, pageSize: size }));
    };

    const handleAdd = () => {
        setEditingId(undefined);
        setModalVisible(true);
    };

    const handleEdit = async (id: number) => {
        const res = await get${bizName}ById(id);
        setEditingId(id);
        // TODO: set form fields from res.data
        setModalVisible(true);
    };

    const handleDelete = async (id: number) => {
        await delete${bizName}(id);
        message.success('删除成功');
        fetchData(searchParams);
    };

    const handleSubmit = async (values: ${bizName}DTO) => {
        setConfirmLoading(true);
        try {
            if (editingId) {
                await update${bizName}(editingId, values);
                message.success('修改成功');
            } else {
                await create${bizName}(values);
                message.success('新增成功');
            }
            setModalVisible(false);
            fetchData(searchParams);
        } finally {
            setConfirmLoading(false);
        }
    };

    const columns: ColumnsType<${bizName}DTO> = [
<#list fields as field>
<#if field.isList?? && field.isList == 1>
<#if field.javaType == 'Boolean'>
        {
            title: '${field.columnComment}',
            dataIndex: '${field.javaFieldName}',
            key: '${field.javaFieldName}',
            render: (val: boolean) => <Switch checked={val} disabled />,
        },
<#else>
        {
            title: '${field.columnComment}',
            dataIndex: '${field.javaFieldName}',
            key: '${field.javaFieldName}',
        },
</#if>
</#if>
</#list>
        {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime',
            width: 180,
        },
        {
            title: '操作',
            key: 'action',
            width: 200,
            render: (_: unknown, record: ${bizName}DTO) => (
                <>
                    <SxwlButton type="link" onClick={() => handleEdit(record.id!)}>编辑</SxwlButton>
                    <SxwlButton type="link" danger onClick={() => handleDelete(record.id!)}>删除</SxwlButton>
                </>
            ),
        },
    ];

    const searchFields = [
<#list fields as field>
<#if field.isQuery?? && field.isQuery == 1>
<#if field.queryFormType == 'Input'>
        { name: '${field.javaFieldName}', label: '${field.columnComment}', component: SxwlInput },
<#elseif field.queryFormType == 'Select'>
        { name: '${field.javaFieldName}', label: '${field.columnComment}', component: SxwlSelect },
</#if>
</#if>
</#list>
    ];

    const formFields = [
<#list fields as field>
<#if field.isInsert?? && field.isInsert == 1>
<#if field.formType == 'Input'>
        { name: '${field.javaFieldName}', label: '${field.columnComment}', component: SxwlInput, rules: [{ required: ${field.isRequired?c} }] },
<#elseif field.formType == 'Select'>
        { name: '${field.javaFieldName}', label: '${field.columnComment}', component: SxwlSelect, rules: [{ required: ${field.isRequired?c} }] },
<#elseif field.formType == 'TextArea'>
        { name: '${field.javaFieldName}', label: '${field.columnComment}', component: 'TextArea', rules: [{ required: ${field.isRequired?c} }] },
<#else>
        { name: '${field.javaFieldName}', label: '${field.columnComment}', component: SxwlInput, rules: [{ required: ${field.isRequired?c} }] },
</#if>
</#if>
</#list>
    ];

    return (
        <SxwlPage>
            <SxwlSearchForm fields={searchFields} onSearch={handleSearch} />
            <SxwlButton type="primary" onClick={handleAdd}>新增${bizNameCn}</SxwlButton>
            <SxwlPage.Table
                columns={columns}
                dataSource={dataSource}
                loading={loading}
                rowKey="id"
                pagination={{ current, pageSize, total, onChange: handlePageChange }}
            />
            <SxwlFormModal
                title={editingId ? '编辑${bizNameCn}' : '新增${bizNameCn}'}
                visible={modalVisible}
                confirmLoading={confirmLoading}
                fields={formFields}
                onSubmit={handleSubmit}
                onCancel={() => setModalVisible(false)}
            />
        </SxwlPage>
    );
};

export default ${bizName}Page;
