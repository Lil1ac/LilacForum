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

const formatTime = (time: string): string => {
    const messageDate = new Date(time);
    const currentDate = new Date();

    // 判断是否为今天
    const isToday = messageDate.toDateString() === currentDate.toDateString();

    if (isToday) {
        // 如果是今天，返回时间
        return messageDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } else {
        // 如果是之前的日期，返回日期和时间
        return messageDate.toLocaleString([], {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }
};

export default { formatDate, stripHtmlTags, formatTime };