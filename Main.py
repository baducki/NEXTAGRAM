from flask import Flask, request, send_file;
from flaskext.mysql import MySQL;
from gcm import GCM;
import json;
import os

API_KEY = "AIzaSyBPue7Sih78oIym7tt3NDMWN7A8Sqi3awA"
reg_id = ""

UPLOAD_FOLDER = "/Users/YG/Documents/workspace/python/image/"


app = Flask(__name__);
mysql = MySQL();


app.config['MYSQL_DATABASE_USER'] = 'root';
app.config['MYSQL_DATABASE_PASSWORD'] = '141057';
app.config['MYSQL_DATABASE_DB'] = 'nextagram';


mysql.init_app(app);

ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])
def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS

@app.route("/gcm", methods=["POST"])
def regGcm():
	if request.method == 'POST':
		reg_id = request.form['reg_id']

		con = mysql.connect()
		cursor = con.cursor()

		query = "insert into regDB \
		(_id) values \
		('" + reg_id + "');";

		cursor.execute(query);
		con.commit();

		return "ok"

	return "ERROR"           

@app.route("/")
def helloWorld():
	return "HelloWorld";

@app.route("/loadData", methods=["GET", "POST"])
def loadData():
	req = request.args.get("ArticleNumber")
	cursor = mysql.connect().cursor();

	if req is None:
		cursor.execute("select * from next_android_nextagram")

	else :
		#cursor.execute("select * from next_android_nextagram where ArticleNumber = " + req)
		cursor.execute("select * from next_android_nextagram")

	result = []
	columns = tuple( [d[0] for d in cursor.description] )

	for row in cursor:
		result.append(dict(zip(columns, row)))

	print(result);

	return json.dumps(result);

@app.route("/upload", methods=["POST"])
def upload():

	if request.method == 'POST':
		title = request.form['Title']
		writer = request.form['Writer']
		id = request.form['Id']
		content = request.form['Content']
		writeDate = request.form['WriteDate']
		imgName = request.form['ImgName']
		print(title);
		
		file = request.files['uploadedfile']
		path = UPLOAD_FOLDER + file.filename;

		if file and allowed_file(file.filename):
			file.save(path)

			con = mysql.connect();
			cursor = con.cursor();
			
			query = "insert into next_android_nextagram \
			(Title, Writer, Id, Content, WriteDate, ImgName) values \
			('" + title + "', '" + writer + "', '" + id + "', '" + content + "', \
				'" + writeDate + "', '" + imgName + "');";

			#print(query);
			cursor.execute(query);
			con.commit();

			return "ok";

	return "error";

@app.route("/image/<fileName>", methods=["GET", "POST"])
def loadImage(fileName):
	print("fileName:"+fileName);
	return send_file(UPLOAD_FOLDER+fileName, mimetype='image');

if __name__ == "__main__":
	app.run(debug=True, host='0.0.0.0', port=5009);
