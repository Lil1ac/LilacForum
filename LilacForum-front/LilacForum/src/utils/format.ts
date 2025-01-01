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

    // 判断是否为昨天
    const isYesterday = (currentDate.getDate() - messageDate.getDate()) === 1
        && currentDate.getMonth() === messageDate.getMonth()
        && currentDate.getFullYear() === messageDate.getFullYear();

    // 判断是否为本周（除了昨天）
    const isThisWeek = messageDate.getFullYear() === currentDate.getFullYear() &&
        Math.floor(messageDate.getDate() / 7) === Math.floor(currentDate.getDate() / 7);

    // 判断是否为本年
    const isThisYear = messageDate.getFullYear() === currentDate.getFullYear();

    if (isToday) {
        // 如果是今天，返回时间
        return messageDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } else if (isYesterday) {
        // 如果是昨天，返回 "昨天" + 时间
        return `昨天 ${messageDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
    } else if (isThisWeek) {
        // 如果是本周（除了昨天），返回 "周几" + 时间
        return `${messageDate.toLocaleString([], { weekday: 'short' })} ${messageDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
    } else if (isThisYear) {
        // 如果是本年，返回 "月/日" + 时间
        return `${messageDate.toLocaleString([], { month: '2-digit', day: '2-digit' })} ${messageDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
    } else {
        // 其他情况，返回完整的日期时间
        return `${messageDate.toLocaleString([], { year: 'numeric', month: '2-digit', day: '2-digit' })} ${messageDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
    }
};



export default { formatDate, stripHtmlTags, formatTime };