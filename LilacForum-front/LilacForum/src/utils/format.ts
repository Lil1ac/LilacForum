const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString();
};
const stripHtmlTags = (htmlContent: string): string => {
    // 替换 <p> 和 <br> 标签为换行符
    const withNewlines = htmlContent
        .replace(/<\/p>/gi, '\n')
        .replace(/<p[^>]*>/gi, '\n')
        .replace(/<br\s*\/?>/gi, '\n');

    // 移除其他 HTML 标签
    return withNewlines.replace(/<[^>]*>/g, '').trim();
};
export default { formatDate, stripHtmlTags };