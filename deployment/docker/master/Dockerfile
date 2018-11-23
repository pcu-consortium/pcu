FROM pcu/spark-base:2.4.0-hadoop2.7

COPY master.sh /
COPY jupyter.sh /

#ENV SPARK_MASTER_PORT 7077
#ENV SPARK_MASTER_WEBUI_PORT 8080
ENV SPARK_MASTER_LOG /spark/logs

#EXPOSE 8080 7077 6066 8888
EXPOSE 8888

RUN jupyter notebook --generate-config
COPY jupyter_notebook_config.py /root/.jupyter/jupyter_notebook_config.py

# Add Tini. Tini operates as a process subreaper for jupyter. This prevents
# kernel crashes.
ENV TINI_VERSION v0.6.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /usr/bin/tini
RUN chmod +x /usr/bin/tini
ENTRYPOINT ["/usr/bin/tini", "--"]

CMD ["/bin/bash", "/master.sh","jupyter"]
#CMD ["jupyter", "notebook", "--port=8888", "--no-browser", "--ip=0.0.0.0", "--allow-root", "--NotebookApp.allow_password_change=True", "--NotebookApp.password='sha1:1012ea89ce58:c90ec31a32b534ce682cd2cbed19cb2234976348'","--ContentsManager.root_dir='/root/'"]

