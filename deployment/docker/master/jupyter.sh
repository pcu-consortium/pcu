#!/bin/bash

jupyter notebook --port=8888 --no-browser --ip=0.0.0.0 --allow-root --NotebookApp.allow_password_change=True --NotebookApp.password='sha1:1012ea89ce58:c90ec31a32b534ce682cd2cbed19cb2234976348' --ContentsManager.root_dir='/root/'