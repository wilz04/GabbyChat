class Client {
	
	private var alias: String;
	private var socket: XMLSocket;
	
	public function Client() {
		alias = "";
		_global.submit = false;
		socket = new XMLSocket();
		socket.connect("127.0.0.1", 3000);
		socket.onData = read;
	}
	
	public function getAlias(): String {
		return alias;
	}
	
	public function login(alias: String, pwd: String): Boolean {
		if ((alias != "") && (pwd != "")) {
			this.alias = alias;
			socket.send("login|" + alias + "|" + pwd + "\n");
			_global.alert = "";
			return _global.submit;
		} else {
			_global.alert = "Debes escribir tus datos";
			return false;
		}
	}
	
	private function read(msg: String): Void {
		var sentence: Array = new Array();
		var tmp:String = "";
		var i:Number = 0;
		var j:Number = 0;
		for (i=0; i<=msg.length; i++) {
			if ((msg.charAt(i) != '|') && (msg.charAt(i) != '\0')) {
				tmp += msg.charAt(i);
			} else {
				sentence[j] = tmp;
				tmp = "";
				j++;
			}
		}
		switch (sentence[0]) {
			case "login":
				if (sentence[1] eq "1") {
					_global.submit = true;
				} else {
					_global.submit = false;
				}
				break;
			case "alert":
				_global.alert = sentence[1];
				break;
			case "getOut":
				_global.getOut(sentence[1], "Conversación: " + sentence[1] + " - " + sentence[2]);
				break;
			case "write":
				if (sentence[1] eq "all") {
					_global.textArea.text += sentence[2];
				} else {
					for (i=0; i<_global.outs.length; i++) {
						if (_global.outs[i].content.alias eq sentence[1]) {
							_global.outs[i].content.taText.text += sentence[2];
						}
					}
				}
				break;
			case "setUp":
				_global.clients = sentence[1];
				break;
			case "add":
				if (_global.clients != "") {
					_global.clients += ">";
				}
				_global.clients += sentence[1];
		}
	}
	
	public function write(msg: String): Void {
		socket.send(msg + "\n");
	}
	
	public function exit(): Void {
		write("exit");
		socket.close();
	}
	
}
