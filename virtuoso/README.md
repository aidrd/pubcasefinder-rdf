# PCF Virtuoso 環境
PubCaseFinder の データ更新用 Virtuoso を Docker コンテナで立ち上げるための手順です 
## 必要環境
Linux または MacOS を想定
### docker  
```
$ docker -version
```
### docker-compose (docker に含まれていないなら)  
  `docker compose` コマンドが使用できる docker のバージョンであればインストールは不要。以下コマンドでバージョンが返ってくれば docker-compose のインストールは不要
```
$ docker compose ps
Docker Compose version v2.27.0
```
`docker compose` コマンドが使用できず、`docker-compose` を別途インストールする。この場合には以降のコマンドをハイフン付きに読み替えて実行
```
$ docker-compose ps
```

## 環境構築
### DB ファイル格納ディレクトリをを作成
virutoso.ini や virutoso.db が格納されるディレクトリを作成。`docker-compose.yml` が置いてあるディレクトリに作成すること。
```
$ mkdir -p database
```
### .env ファイル作成
構築環境の合わせて環境変数を設定する為の `.env` ファイルを作成。この名前で設定しておくと docker-compose.yml に書かれた変数に値が格納されて起動する。
```
$ vi .env
```
#### RDF_DIR
ロードするRDF格納ディレクトリを設定する。コンテナ内の `/rdf_data/` にマウントされる
#### SCRIPTS_DIR
ロードコマンド等のスクリプトファイルを確認するディレクトリを設定する。コンテナ内の `/scripts/` にマウントされる
#### ENDPOINT_PORT
SPARQL endpoint のポート `8890` のホスト側露出ポート番号を指定。設定しなければ `8890` で設定される

#### UID
コンテナ実行ユーザ ID を指定 `id` コマンドで現在のユーザ ID(uid) は確認可能。省略すると root で実行される
```
$ id
```
#### DOCKER_GID
docker の group_idを指定。ユーザ ID と同様に `id` コマンドで確認可能。省略すると root で実行される

### コンテナ起動
`docker-compose.yml` ファイルが置かれているディレクトリでコンテナ作成 & 起動コマンドを実行
```
$ docker compose up -d
```

## 運用時の手順
### コンテナの起動停止手順
全て `docker-compose.yml` の置いてあるディレクトリでコマンドを実行する。`docker-compose` をインストールした場合は、ハイフン付きの`docker-compose`コマンドに読み替えて実行する
### コンテナ作成&起動(初回起動)
```
$ docker compose up -d
```
#### コンテナ停止
```
$ docker compose stop
```
#### コンテナ開始(再開)
```
$ docker compose start
```
#### コンテナ削除
```
$ docker compose down
```
#### コンテナ状態確認
```
$ docker compose ps
```

### データのロード
#### RDFファイルの格納
`.env` ファイルで指定した `$RDF_DIR` の下に RDF ファイルを格納する。このディレクトリはコンテナ内から `/rdf_data/` としてアクセスが出来る。
ロードスクリプトの記述方法によってもディレクトリ構成は変更されるが、ここでは、グラフごとにロードファイルを格納し、最新のディレクトリに `latest` というシンボリックリンクを貼る。
```
$ cd pcf
$ mkdir -p 20240531
$ cp -a (任意のファイル) 20240531/

# 最新バージョンのディレクトリに latest という名前でリンクを貼る
# これによってロードディレクトリが固定されるのでスクリプトファイルを修正せずロードが出来る
$ ln -snf 20240531 latest
```

#### スクリプトファイルの作成
コンテナ内で `isql` を実行するスクリプトファイルを作成し、`.env` ファイルで指定した `$SCRIPTS_DIR` の下に格納する。

* ロードファイルサンプル (pcf_rdf_load.sh)
以下はあるグラフのデータを入れ替えるスクリプト
```
#!/bin/sh

ISQL_EXEC='/opt/virtuoso-opensource/bin/isql 1111 dba dba exec'

GRAPH_NAME='https://pubcasefinder.dbcls.jp/rdf/pcf'
$ISQL_EXEC="DELETE FROM DB.DBA.LOAD_LIST WHERE ll_graph ='${GRAPH_NAME}';"
$ISQL_EXEC="log_enable(2,1); SPARQL CLEAR GRAPH <${GRAPH_NAME}>;"
$ISQL_EXEC="log_enable(2,1); ld_dir_all('/rdf_data/pcf/rdf/latest', '*.ttl', '${GRAPH_NAME}');"
$ISQL_EXEC="rdf_loader_run();"
$ISQL_EXEC="checkpoint;"
```

#### ロードの実行
コンテナに対してスクリプト実行するコマンドを実行
```
$ docker compose exec virtuoso-update sh /scripts/pcf_rdf_load.sh
```

### isql による操作
コンテナ内にシェルで入る
```
$ docker compose exec virtuoso-update /bin/sh

# ここからはコンテナ内の操作。ファイルの置き場を確認
$ ls -l /rdf_data/
$ ls -l /scripts/

# isqlにログイン
$ isql 1111 dba dba
SQL> exit;

# コンテナからも `exit` で抜ける
$ exit
```

### Virtuoso の設定を変更(バッファ等を変更したい場合)
コンテナ停止
```
$ docker compose stop
```
設定ファイル `virtuoso.ini` を書き換える
```
$ vi dabasese/vittuoso.ini
```
コンテナ起動
```
$ docker compose start
```

### 環境(ポートやディレクトリ構成等)の設定を変更
コンテナ削除
```
$ docker compose down
```
設定ファイル `.env` を書き換える
```
$ vi .env
```
コンテナ(再)作成&起動
```
$ docker compose up -d
```

### virtuoso.db ファイルの保存
一度コンテナを停止して、マウントしている `databases` ディレクトリの `virutoso.db` ファイルを任意の場所にコピー。稼働中の DB ファイルをコピーするとコピー先で正しく起動できない場合がある。またコピー先の Virtuso のバージョンはコピー元のバージョンに近い方が良い。7.2 でロードして 7.1 で立ち上げるとかはおそらく難しい(立ち上がっても応答がおかしいなどの不具合が発生しやすい)。

コンテナ停止
```
$ docker compose stop
```
任意の場所へファイルコピー
```
$ cp databases/virtuoso.db (任意のディレクトリ)
```
コンテナ起動
```
$ docker compose start
```


### virtuoso.db を配置して起動
コピー先の Virtuoso も同等の Docker 環境で立ち上がっていれば、`virutoso.db` ファイルをコピーして立ち上げが可能。更新用の Virtuoso で作成された `virutoso.db` ファイルを dev 環境、 本番環境の Virtuoso にコピーして起動が可能  
コンテナ停止
```
$ docker compose stop
```
`virtuso.ini` 以外の既存のファイルを削除。
```
$ rm database/virtuoso-temp.db database/virtuoso.db database/virtuoso.log database/virtuoso.pxa database/virtuoso.trx
```
`virtuoso.db` ファイルをコピー
```
$ cp (任意のディレクトリ)/virutoso.db databases/virtuoso.db
$ ls databases/
virtuoso.db  virtuoso.ini
```
コンテナ起動
```
$ docker compose start
```

