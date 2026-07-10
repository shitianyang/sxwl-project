import { Table } from 'antd';
import type { TableProps } from 'antd';

export type SxwlTableProps<T> = TableProps<T>;

/**
 * SxwlTable — 基于 antd Table 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlTable rowKey="id" columns={columns} dataSource={data} loading={loading} />
 * ```
 */
const SxwlTable = <T extends object>(props: SxwlTableProps<T>) => <Table<T> {...props} />;

export default SxwlTable;
