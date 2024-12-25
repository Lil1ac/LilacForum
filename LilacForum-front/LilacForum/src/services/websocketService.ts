// websocketService.ts
export class WebSocketService {
  private socket: WebSocket;

  constructor(
    url: string,
    onMessage: (msg: MessageEvent) => void,
    onClose: () => void,
    onError: () => void
  ) {
    this.socket = new WebSocket(url);
    this.socket.onmessage = onMessage;
    this.socket.onclose = onClose;
    this.socket.onerror = onError;
  }

  send(message: object) {
    this.socket.send(JSON.stringify(message));
  }

  close() {
    this.socket.close();
  }
}
export default WebSocketService;