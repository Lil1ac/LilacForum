export interface PageBean<T> {
    data: T[];
    total: number;
    pageSize: number;
    page: number;
  }
  