FROM public.ecr.aws/lambda/nodejs:14

COPY handler/* ./

CMD ["app.lambdaHandler"]
